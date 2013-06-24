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

import bluerpint.sdk.util.Counter;

/**
 * JobQueue for Workers.<br>
 * A Worker Group shares a JobQueue.<br>
 * <b>Thread Safe</b><br>
 * 
 * @param <T> job class
 * @author Simon Lee
 * @since 2008. 11. 25.
 */
public class JobQueue<T> {
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
	 * @throws InterruptedException
	 *             mutex exception
	 */
	public void push(final T aJob) throws InterruptedException {
		mtx.acquire();

		queue.add(aJob);

		// if lock is not in use (i.e. all threads are busy)
		if (!lock.isInuse()) {
			synchronized (allBusyTrapLock) {
				allBusyTrap = true;
			}
		}

		// release lock mutex
		lock.release();

		mtx.release();
	}

	/**
	 * pops a job Object from queue
	 * 
	 * @return a job Object
	 * @throws InterruptedException
	 *             mutex exception
	 */
	public T pop() throws InterruptedException {
		mtx.acquire();

		T aJob;
		while (queue.size() <= 0) {
			// release exclusion mutex
			mtx.release();

			// acquire lock mutex
			lock.acquire();

			// re-acquire exclusion mutex to pop
			mtx.acquire();
		}
		aJob = queue.remove(0);

		mtx.release();

		return aJob;
	}

	/**
	 * returns size of queue
	 * 
	 * @return
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
	 * returns processed jobs count
	 * 
	 * @return
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
	 * 
	 * @throws InterruptedException
	 *             mutex exception
	 * 
	 */
	public void clear() throws InterruptedException {
		mtx.acquire();

		queue.clear();
		resetProcessedJobs();
		allBusyTrap = false;

		mtx.release();
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
