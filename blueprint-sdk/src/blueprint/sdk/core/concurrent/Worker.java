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
import blueprint.sdk.util.queue.Queue;
import org.apache.log4j.Logger;

/**
 * Worker Thread
 *
 * @param <J> job class
 * @author Sangmin Lee
 * @since 2007. 07. 25
 */
public abstract class Worker<J> extends TerminatableThread {
    private static final Logger L = Logger.getLogger(WorkerGroup.class);

    private Queue<J> jobQueue = null;

    /**
     * notify this when terminated
     */
    private final Object deathMonitor;

    private boolean active = false;

    /**
     * Constructor
     *
     * @param jobQueue job queue
     * @param deathMonitor notify this monitor after termination
     */
    public Worker(final Queue<J> jobQueue, final Object deathMonitor) {
        this.jobQueue = jobQueue;
        this.deathMonitor = deathMonitor;
    }

    public void start() {
        Thread thr = new Thread(this);
        thr.setName(this.getClass().getName());
        thr.setDaemon(true);
        thr.start();
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                // blocks until queue have something to pop
                J job = jobQueue.take();
                active = true;
                process(job);
            } catch (Exception e) {
                L.error("Can't process a job", e);
                terminate();
            } finally {
                if (jobQueue instanceof JobQueue) {
                    ((JobQueue<?>) jobQueue).increaseProcessedJobCounter();
                }
                active = false;
            }
        }

        terminated = true;
    }

    /**
     * handles a job or client object
     *
     * @param job a job to process
     */
    protected abstract void process(J job);

    @Override
    public void terminate() {
        running = false;

        synchronized (deathMonitor) {
            deathMonitor.notifyAll();
        }
    }

    /**
     * @return true if current worker is processing a job
     */
    public boolean isActive() {
        return active;
    }

    @Override
    protected void finalize() throws Throwable {
        jobQueue.clear();
        jobQueue = null;

        super.finalize();
    }
}