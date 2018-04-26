/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent.lock.timestamped;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wrapper of {@link ReentrantLock} with timestamp.
 *
 * @author lempel@gmail.com
 * @since 2014. 5. 9.
 */
public class TimestampedLock extends TimestampedLockBase<ReentrantLock> implements Lock {
    /**
     * @param fair fairness for {@link ReentrantLock}
     */
    public TimestampedLock(boolean fair) {
        super(new ReentrantLock(fair));
    }

    @Override
    public void lock() {
        getLock().lock();

        updateTimestamp();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        getLock().lockInterruptibly();

        updateTimestamp();
    }

    @Override
    public boolean tryLock() {
        boolean result = getLock().tryLock();

        if (result) {
            updateTimestamp();
        }

        return result;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        boolean result = getLock().tryLock(time, unit);

        if (result) {
            updateTimestamp();
        }

        return result;
    }

    @Override
    public void unlock() {
        getLock().unlock();

        updateTimestamp();
    }

    @Override
    public Condition newCondition() {
        Condition result = getLock().newCondition();

        updateTimestamp();

        return result;
    }

    /**
     * @return {@link ReentrantLock#isLocked()}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLocked() {
        return getLock().isLocked();
    }
}
