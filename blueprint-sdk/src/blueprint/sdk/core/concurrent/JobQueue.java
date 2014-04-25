/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent;

import java.util.List;
import java.util.Vector;

import blueprint.sdk.core.concurrent.lock.Mutex;
import blueprint.sdk.util.Counter;
import blueprint.sdk.util.queue.Queue;

/**
 * JobQueue for Workers.<br>
 * A Worker Group shares a JobQueue.<br>
 * <b>Thread Safe</b><br>
 * 
 * @param <T>
 *            job class
 * @author Sangmin Lee
 * @since 2008. 11. 25.
 */
public class JobQueue<T> implements Queue<T> {
	/** mutex for exclusion */
	private final Mutex mtx = new Mutex();
	/** mutex for lock */
	private final Mutex lock = new Mutex();
	/** actual job queue */
	private final List<T> queue = new Vector<T>();
	/** counts how many jobs are processed */
	private final Counter processedJobs = new Counter();

	/** is all threads busy situation occured? */
	private boolean allBusyTrap = false;
	/** lock for allBusyTrap flag */
	private final Object allBusyTrapLock = new Object();
	/** start/stop count */
	private boolean count = false;;

	/**
	 * push a job Object to queue<br>
	 * 
	 * @param aJob
	 */
	public void push(final T aJob) {
		mtx.lock();
		try {
			queue.add(aJob);

			// if lock is not in use (i.e. all threads are busy)
			if (!lock.isLocked()) {
				synchronized (allBusyTrapLock) {
					allBusyTrap = true;
				}
			}

			try {
				// release lock mutex
				lock.unlock();
			} catch (IllegalMonitorStateException ignored) {
				// can happen if there's no waiting worker
			}
		} finally {
			mtx.unlock();
		}
	}

	/**
	 * pops a job Object from queue
	 * 
	 * @return a job Object
	 */
	public T take() {
		T aJob = null;

		mtx.lock();
		try {
			while (queue.size() <= 0) {
				// release exclusion mutex
				mtx.unlock();

				// acquire lock mutex
				lock.lock();

				// re-acquire exclusion mutex to pop
				mtx.lock();
			}

			aJob = queue.remove(0);
		} finally {
			mtx.unlock();
		}

		return aJob;
	}

	/**
	 * @return size of queue
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * is all threads busy situation occured?
	 * 
	 * @return true: yes
	 */
	public boolean isAllBusyTrapped() {
		synchronized (allBusyTrapLock) {
			return allBusyTrap;
		}
	}

	/**
	 * reset allBusyTrap flag
	 */
	protected void resetAllBusyTrap() {
		synchronized (allBusyTrapLock) {
			allBusyTrap = false;
		}
	}

	/**
	 * start/stop count.<br>
	 * this method is called by WorkerGroup<br>
	 * 
	 * @param flag
	 */
	protected void setCount(boolean flag) {
		this.count = flag;
	}

	/**
	 * counts a processed job<br>
	 * this method is called by Worker<br>
	 * 
	 */
	protected void increaseProcessedJobCounter() {
		if (count) {
			processedJobs.increase();
		}
	}

	/**
	 * @return processed jobs count
	 */
	protected long getProcessedJobs() {
		return processedJobs.count();
	}

	/**
	 * reset processed jobs count
	 */
	protected void resetProcessedJobs() {
		processedJobs.reset();
	}

	/**
	 * clears queue
	 */
	public void clear() {
		mtx.lock();
		try {
			queue.clear();
			resetProcessedJobs();
			allBusyTrap = false;
		} finally {
			mtx.unlock();
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
