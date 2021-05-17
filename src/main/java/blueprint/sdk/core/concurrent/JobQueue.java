/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.core.concurrent;

import blueprint.sdk.core.concurrent.lock.Mutex;
import blueprint.sdk.util.Counter;
import blueprint.sdk.util.queue.Queue;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JobQueue for Workers.<br>
 * A Worker Group shares a JobQueue.<br>
 * <b>Thread Safe</b><br>
 *
 * @param <T> job class
 * @author lempel@gmail.com
 * @since 2008. 11. 25.
 */
public class JobQueue<T> implements Queue<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Mutex mutex = new Mutex();
    /**
     * actual job queue
     */
    private final LinkedList<T> queue = new LinkedList<>();
    /**
     * counts how many jobs are processed
     */
    private final Counter processedJobs = new Counter();
    private boolean idle = false;
    /**
     * start/stop count
     */
    private boolean count = false;
    /**
     * flag for disposed queue
     */
    private boolean disposed = false;

    /**
     * push a job Object to queue<br>
     *
     * @param aJob job to push
     */
    public void push(final T aJob) {
        lock.lock();
        try {
            if (disposed) {
                throw new IllegalStateException("Can't push into disposed queue");
            } else {
                queue.add(aJob);

                if (mutex.isLocked()) {
                    mutex.unlock();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * pops a job Object from queue
     *
     * @return a job Object
     */
    public T take() {
        T aJob = null;

        lock.lock();
        try {
            while (queue.size() == 0 || (aJob = queue.pop()) == null) {
                idle = true;
                lock.unlock();

                mutex.lock();

                lock.lock();
                idle = false;
            }
        } finally {
            lock.unlock();
        }

        return aJob;
    }

    /**
     * @return size of queue
     */
    public int size() {
        return queue.size();
    }

    public boolean isIdle() {
        lock.lock();
        try {
            return idle;
        } finally {
            lock.unlock();
        }
    }

    /**
     * start/stop count.<br>
     * this method is called by WorkerGroup<br>
     *
     * @param flag set to start counting processed jobs
     */
    @SuppressWarnings("SameParameterValue")
    void setCount(boolean flag) {
        this.count = flag;
    }

    /**
     * counts a processed job<br>
     * this method is called by Worker<br>
     */
    void increaseProcessedJobCounter() {
        if (count) {
            processedJobs.increase();
        }
    }

    /**
     * @return processed jobs count
     */
    long getProcessedJobs() {
        return processedJobs.count();
    }

    /**
     * reset processed jobs count
     */
    void resetProcessedJobs() {
        processedJobs.reset();
    }

    /**
     * Release all blocked Threads on {@link JobQueue#take()} and dispose queue.
     */
    public void dispose() {
        lock.lock();
        try {
            disposed = true;

            if (mutex.isLocked()) {
                mutex.unlock();
            }
            idle = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * clears queue
     */
    public void clear() {
        lock.lock();
        try {
            queue.clear();

            resetProcessedJobs();
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc, override method)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        queue.clear();

        super.finalize();
    }
}
