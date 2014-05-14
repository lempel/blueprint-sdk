/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent.lock.timestamped;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * Base class for TimestampedLock and TimestampedReadWriteLock
 * 
 * @author Sangmin Lee
 * @since 2014. 5. 9.
 */
public class TimestampedLockBase<T> {
	/** Timestamp of System.currentTimeMillis() */
	protected long timestamp = System.currentTimeMillis();

	/** Actual lock */
	private T lock;

	/**
	 * @param lock
	 *            Actual {@link Lock} or {@link ReadWriteLock}
	 */
	public TimestampedLockBase(T lock) {
		this.lock = lock;

		if (lock == null) {
			throw new NullPointerException("Provided lock can't be null");
		}
	}

	/**
	 * @return Actual lock
	 */
	protected final T getLock() {
		return lock;
	}

	/**
	 * @return Timestamp of System.currentTimeMillis()
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Update timestamp with current time using System.currentTimeMillis()
	 */
	public void updateTimestamp() {
		timestamp = System.currentTimeMillis();
	}

	/**
	 * @param delta
	 *            time in millisecond
	 * @return true if timestamp is older than '
	 *         {@link System#currentTimeMillis()} - delta'
	 */
	public boolean isTimedOut(long delta) {
		return (getTimestamp() < System.currentTimeMillis() - delta);
	}
}
