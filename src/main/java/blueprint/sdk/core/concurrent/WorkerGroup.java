/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.core.concurrent;

import blueprint.sdk.util.jvm.shutdown.TerminatableThread;
import blueprint.sdk.util.jvm.shutdown.Terminator;
import blueprint.sdk.util.queue.Queue;
import blueprint.sdk.util.reflect.Crowbar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Group of Workers<br>
 * Maintains JobQueue and Workers<br>
 * <br>
 * If you want to use automatic thread spanning feature, call start() method.<br>
 *
 * @param <J> Job Type
 * @param <Q> Queue Type
 * @author lempel@gmail.com
 * @since 2008. 11. 25.
 */
public class WorkerGroup<J, Q extends Queue<J>> extends TerminatableThread {
    /**
     * check interval (msec)
     */
    protected static final int INTERVAL = 1000;
    /**
     * worker thread increase ratio
     */
    static final float THREAD_INC_RATIO = 0.2f;
    private static final Logger L = LoggerFactory.getLogger(WorkerGroup.class);
    protected final Class<? extends Worker<J>> workerClass;
    protected final Q jobQueue;
    protected final List<Worker<J>> workers;
    /**
     * monitor for dead workers
     */
    protected final Object deathMonitor = new Object();
    final int initialWorkers;

    /**
     * Constructor
     *
     * @param jobQueue    job queue
     * @param workerClass Worker class
     * @param workerCount Initial number of workers
     */
    public WorkerGroup(final Q jobQueue, final Class<? extends Worker<J>> workerClass, final int workerCount) {
        // register to shutdown hook (Terminator)
        Terminator.getInstance().register(this);

        this.jobQueue = jobQueue;
        this.workers = new ArrayList<>(workerCount);
        this.workerClass = workerClass;
        initialWorkers = workerCount;

        setName(this.getClass().getName());
        setDaemon(true);

        L.info("worker group created - worker: {}, count: {}", workerClass, workerCount);
    }

    /**
     * create and add a new Worker
     *
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void newWorker() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Worker<J> aWorker;

        Constructor[] constructors = workerClass.getConstructors();
        for (Constructor constructor : constructors) {
            Parameter[] params = constructor.getParameters();

            if (params == null || params.length != 2) {
                continue;
            }

            if (Crowbar.isInstance(params[0].getType(), Queue.class)
                    && Crowbar.isInstance(params[1].getType(), Object.class)) {
                // FIXME how can I resolve this warning?
                aWorker = (Worker<J>) constructor.newInstance(jobQueue, deathMonitor);
                workers.add(aWorker);
                aWorker.start();
                break;
            }
        }
    }


    /**
     * Add more workers
     *
     * @param count number of workers
     */
    void addWorkers(final int count) {
        int failure = 0;

        synchronized (workers) {
            for (int i = 0; i < count; i++) {
                try {
                    newWorker();
                } catch (Exception e) {
                    L.error("worker creation failed - " + e);
                    failure++;
                }
            }
        }

        int diff = (count - failure);
        if (diff > 0) {
            L.info("worker added - class: {}, count: {} (+{})", workerClass, workers.size(), diff);
        }
    }

    /**
     * Remove some workers.<br>
     * <br>
     * Removed workers are subjected to terminate. <b>Not immediate.</b><br>
     * <br>
     * <b>You can't remove all workers.</b> At least 1 worker will survive.<br>
     *
     * @param count number of workers
     */
    void removeWorkers(int count) {
        int removed = 0;

        synchronized (workers) {
            for (int i = 0; i < count && workers.size() > 1; i++) {
                workers.remove(0).terminate();
                removed++;
            }
        }

        if (removed > 0) {
            L.info("worker removed - class: {}, count: {} (-{})", workerClass, workers.size(), removed);
        }
    }

    @Override
    public void terminate() {
        running = false;

        synchronized (deathMonitor) {
            deathMonitor.notifyAll();
        }

        if (workers != null) {
            synchronized (workers) {
                for (Worker<J> worker : workers) {
                    worker.terminate();
                }
            }
        }
    }

    public void addJob(final J job) {
        jobQueue.push(job);
    }

    @Override
    public void run() {
        try {
            // instantiate & start workers
            for (int i = 0; i < initialWorkers; i++) {
                newWorker();
            }

            running = true;
        } catch (Exception e) {
            L.error("Can't create workers. Terminating " + getClass().getSimpleName(), e);
        }

        while (running) {
            maintainWorkers();

            synchronized (deathMonitor) {
                try {
                    deathMonitor.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        terminated = true;
    }

    /**
     * Check all workers and replace terminated with new ones.
     */
    void maintainWorkers() {
        synchronized (workers) {
            int workerCount = workers.size();

            Iterator<Worker<J>> iter = workers.iterator();
            while (iter.hasNext()) {
                Worker<?> worker = iter.next();

                if (worker.isTerminated()) {
                    iter.remove();
                }
            }

            int delta = workerCount - workers.size();
            for (int i = 0; i < delta; i++) {
                try {
                    newWorker();
                } catch (Exception e) {
                    L.warn("Can't create new worker - " + workerClass.getName(), e);
                }
            }
        }
    }

    public String getActivity() {
        return "queued=" + jobQueue.size() + ", workers=" + workers.size() + ", processing=" + getActiveWorkerCount();
    }

    int getActiveWorkerCount() {
        int result = 0;

        for (Worker<J> worker : workers) {
            if (worker.isActive()) {
                result++;
            }
        }

        return result;
    }
}
