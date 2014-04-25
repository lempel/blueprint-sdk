package blueprint.sdk.core.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Non-reentrant mutual exclusion lock
 * 
 * @author Sangmin Lee
 * @since 2014. 4. 25.
 */
public class Mutex {
	/** synchronizer */
	protected Sync sync;

	public Mutex() {
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
	 * Synchronizer for {@link Mutex}
	 * 
	 * @author Sangmin Lee
	 * @since 2014. 4. 24.
	 */
	class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 3584438822993449109L;

		@Override
		protected boolean tryAcquire(int arg) {
			boolean result = false;

			if (getState() == 0 && compareAndSetState(0, arg)) {
				setExclusiveOwnerThread(Thread.currentThread());
				result = true;
			}

			return result;
		}

		@Override
		protected boolean tryRelease(int arg) {
			boolean result = false;

			if (getState() == 1 && compareAndSetState(1, arg)) {
				setExclusiveOwnerThread(null);
				result = true;
			}

			return result;
		}

		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}
	}
}
