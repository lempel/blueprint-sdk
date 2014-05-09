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
 * Timestamp will be updated by every invocation of
 * {@link TimestampedMutex.Sync#tryAcquire(int)} and
 * {@link TimestampedMutex.Sync#tryRelease(int)} with
 * {@link System#currentTimeMillis()}<br/>
 * 
 * @author Sangmin Lee
 * @since 2014. 4. 25.
 */
public class TimestampedMutex {
	/** synchronizer */
	protected Sync sync;

	public TimestampedMutex() {
		sync = new Sync();
	}

	/**
	 * Acquires lock
	 */
	public void lock() {
		sync.lock(null, 1);
	}

	/**
	 * Acquires lock
	 * 
	 * @param requester
	 *            requester object for re-enterance
	 */
	public void lock(Object requester) {
		sync.lock(requester, 1);
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
	 * @return current lock owner
	 */
	public Object getOwner() {
		return sync.getOwner();
	}

	/**
	 * @return current timestamp
	 */
	public long getTimestamp() {
		return sync.timestamp;
	}

	/**
	 * Synchronizer for {@link TimestampedMutex}
	 * 
	 * @author Sangmin Lee
	 * @since 2014. 4. 24.
	 */
	class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 3584438822993449109L;

		long timestamp = System.currentTimeMillis();

		/** Lock owner */
		Object owner = null;

		/**
		 * Acquires lock
		 * 
		 * @param requester
		 *            requester object for re-enterance
		 * @param arg
		 *            the acquire argument.<br/>
		 *            See {@link AbstractQueuedSynchronizer#acquire(int)}<br/>
		 */
		public void lock(Object requester, int arg) {
			if (!isReentrable(requester)) {
				acquire(arg);
				synchronized (this) {
					owner = requester;
				}
			}
		}

		@Override
		protected boolean tryAcquire(int arg) {
			boolean result = false;

			if (compareAndSetState(0, arg)) {
				setExclusiveOwnerThread(Thread.currentThread());
				result = true;
			}

			updateTimestamp();
			return result;
		}

		@Override
		protected boolean tryRelease(int arg) {
			if (getState() == 0) {
				throw new IllegalMonitorStateException();
			}

			synchronized (this) {
				owner = null;
				setExclusiveOwnerThread(null);
				setState(0);
			}

			updateTimestamp();
			return true;
		}

		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}

		/**
		 * See if re-entrable or not.
		 * 
		 * @param requester
		 *            requester object for re-enterance
		 * @return true if can re-enter
		 */
		boolean isReentrable(Object requester) {
			boolean result = false;

			if (owner == null) {
				if (requester == null) {
					result = true;
				}
			} else {
				if (owner.equals(requester)) {
					result = true;
				}
			}

			return result;
		}

		/**
		 * @return current lock owner
		 */
		Object getOwner() {
			return owner;
		}

		/**
		 * update timestamp with System.currentTimeMillis()
		 */
		void updateTimestamp() {
			timestamp = System.currentTimeMillis();
		}

		/**
		 * @return current timestamp
		 */
		long getStamp() {
			return timestamp;
		}
	}
}
