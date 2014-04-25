package blueprint.sdk.core.concurrent.lock;

import java.util.HashSet;
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

	public int getParkThreads() {
		return sync.parked.size();
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

		HashSet<Integer> parked = new HashSet<Integer>();
		
		@Override
		protected boolean tryAcquire(int arg) {
			boolean result = false;

			Thread t = Thread.currentThread();
			if (compareAndSetState(0, arg)) {
				setExclusiveOwnerThread(t);
				result = true;
			} else {
				parked.add(t.hashCode());
			}

			return result;
		}

		@Override
		protected boolean tryRelease(int arg) {
			if (getState() == 0) {
				throw new IllegalMonitorStateException();
			}
			
			parked.remove(getExclusiveOwnerThread().hashCode());
			setExclusiveOwnerThread(null);
			setState(0);

			return true;
		}

		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}
	}
}
