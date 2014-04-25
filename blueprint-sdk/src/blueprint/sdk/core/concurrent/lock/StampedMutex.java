/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Mutex with timestamp.<br/>
 * <br/>
 * Timestamp will be updated by every invocation of {@link Sync#tryAcquire(int)}
 * and {@link Sync#tryRelease(int)} with {@link System#currentTimeMillis()}<br/>
 * 
 * @author Sangmin Lee
 * @since 2014. 4. 25.
 */
public class StampedMutex {
	/** synchronizer */
	protected Sync sync;

	public StampedMutex() {
		sync = new Sync();
	}

	/**
	 * Acquires lock
	 */
	public void lock() {
		sync.acquire(1);
	}

	/**
	 * Releases lock
	 */
	public void unlock() {
		sync.release(1);
	}

	/**
	 * @return true if locked
	 */
	public boolean isLocked() {
		return sync.isHeldExclusively();
	}

	/**
	 * @return current timestamp
	 */
	public long getTimestamp() {
		return sync.timestamp;
	}

	/**
	 * Synchronizer for {@link Mutex}
	 * 
	 * @author Sangmin Lee
	 * @since 2014. 4. 24.
	 */
	class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 3584438822993449109L;

		long timestamp = System.currentTimeMillis();

		@Override
		protected boolean tryAcquire(int arg) {
			boolean result = false;

			if (compareAndSetState(0, arg)) {
				setExclusiveOwnerThread(Thread.currentThread());
				result = true;
			}

			timestamp = System.currentTimeMillis();
			return result;
		}

		@Override
		protected boolean tryRelease(int arg) {
			if (getState() == 0) {
				throw new IllegalMonitorStateException();
			}

			setExclusiveOwnerThread(null);
			setState(0);

			timestamp = System.currentTimeMillis();
			return true;
		}

		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}

		/**
		 * @return current timestamp
		 */
		long getTimestamp() {
			return timestamp;
		}
	}
}
