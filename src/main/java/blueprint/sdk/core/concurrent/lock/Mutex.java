/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.core.concurrent.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Non-reentrant mutual exclusion lock
 *
 * @author lempel@gmail.com
 * @since 2014. 4. 25.
 */
public class Mutex {
    /**
     * synchronizer
     */
    private final Sync sync;

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
     * @author lempel@gmail.com
     * @since 2014. 4. 24.
     */
    class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 3584438822993449109L;

        @Override
        protected boolean tryAcquire(int arg) {
            boolean result = false;

            if (compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(Thread.currentThread());
                result = true;
            }

            return result;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }

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
