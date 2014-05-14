/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent.lock.timestamped;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Wrapped {@link ReentrantReadWriteLock} with timestamp support.
 * 
 * @author Sangmin Lee
 * @since 2014. 5. 9.
 */
public class TimestampedReadWriteLock extends TimestampedLockBase<ReentrantReadWriteLock> implements ReadWriteLock {
	/** wrapped {@link ReadWriteLock#readLock()} */
	protected ReadLock readLock;
	/** wrapped {@link ReadWriteLock#writeLock()} */
	protected WriteLock writeLock;

	public TimestampedReadWriteLock() {
		super(new ReentrantReadWriteLock(true));

		readLock = new ReadLock(getLock().readLock());
		writeLock = new WriteLock(getLock().writeLock());
	}

	@Override
	public ReadLock readLock() {
		return readLock;
	}

	@Override
	public WriteLock writeLock() {
		return writeLock;
	}

	/**
	 * @return {@link ReentrantReadWriteLock#isWriteLocked()}
	 */
	public boolean isWriteLocked() {
		return getLock().isWriteLocked();
	}

	/**
	 * Wrapper for {@link ReentrantReadWriteLock.ReadLock} with timestamp
	 * support.
	 * 
	 * @author Sangmin Lee
	 * @since 2014. 5. 9.
	 */
	// FIXME must extract a super class
	public class ReadLock implements Lock {
		/** Actual lock */
		ReentrantReadWriteLock.ReadLock lock;

		/**
		 * @param lock
		 *            Actual Lock
		 */
		public ReadLock(ReentrantReadWriteLock.ReadLock lock) {
			this.lock = lock;
		}

		@Override
		public void lock() {
			lock.lock();

			updateTimestamp();
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			lock.lockInterruptibly();

			updateTimestamp();
		}

		@Override
		public boolean tryLock() {
			boolean result = lock.tryLock();

			if (result) {
				updateTimestamp();
			}

			return result;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			boolean result = lock.tryLock(time, unit);

			if (result) {
				updateTimestamp();
			}

			return result;
		}

		@Override
		public void unlock() {
			lock.unlock();

			updateTimestamp();
		}

		@Override
		public Condition newCondition() {
			Condition result = lock.newCondition();

			updateTimestamp();

			return result;
		}
	}

	/**
	 * Wrapper for {@link ReentrantReadWriteLock.WriteLock} with timestamp
	 * support.
	 * 
	 * @author Sangmin Lee
	 * @since 2014. 5. 9.
	 */
	// FIXME must extract a super class
	public class WriteLock implements Lock {
		/** Actual lock */
		ReentrantReadWriteLock.WriteLock lock;

		/**
		 * @param lock
		 *            Actual Lock
		 */
		public WriteLock(ReentrantReadWriteLock.WriteLock lock) {
			this.lock = lock;
		}

		@Override
		public void lock() {
			lock.lock();

			updateTimestamp();
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			lock.lockInterruptibly();

			updateTimestamp();
		}

		@Override
		public boolean tryLock() {
			boolean result = lock.tryLock();

			if (result) {
				updateTimestamp();
			}

			return result;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			boolean result = lock.tryLock(time, unit);

			if (result) {
				updateTimestamp();
			}

			return result;
		}

		@Override
		public void unlock() {
			lock.unlock();

			updateTimestamp();
		}

		@Override
		public Condition newCondition() {
			Condition result = lock.newCondition();

			updateTimestamp();

			return result;
		}
	}
}
