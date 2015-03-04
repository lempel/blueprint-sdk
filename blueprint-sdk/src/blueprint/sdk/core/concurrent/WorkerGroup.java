/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent;

import blueprint.sdk.util.jvm.shutdown.TerminatableThread;
import blueprint.sdk.util.jvm.shutdown.Terminator;
import blueprint.sdk.util.queue.Queue;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
 * @author Sangmin Lee
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
    private static final Logger L = Logger.getLogger(WorkerGroup.class);
    protected final Class<? extends Worker<J>> workerClass;
    final int initialWorkers;
    protected final Q jobQueue;
    protected final List<Worker<J>> workers;
    /**
     * monitor for dead workers
     */
    protected final Object deathMonitor = new Object();

    /**
     * Constructor
     *
     * @param jobQueue job queue
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

        L.info("worker group created - class: " + workerClass + ", count: " + workerCount);
    }

    /**
     * create & add a new Worker
     *
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void newWorker() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Worker<J> aWorker;
        Constructor<? extends Worker<J>> cons = workerClass.getConstructor(Queue.class, Object.class);
        aWorker = cons.newInstance(jobQueue, deathMonitor);
        workers.add(aWorker);
        aWorker.start();
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

        L.info("worker added - class: " + workerClass + ", count: " + (count - failure));
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

        L.info("worker removed - class: " + workerClass + ", count: " + removed);
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
