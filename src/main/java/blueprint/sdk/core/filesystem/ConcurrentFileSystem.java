/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.core.filesystem;

import blueprint.sdk.core.concurrent.lock.timestamped.TimestampedLock;
import blueprint.sdk.util.jvm.shutdown.TerminatableThread;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread Safe File System.<br>
 * <br>
 * For each and every file, a {@link TimestampedLock} is created for
 * synchronization.<br>
 * Once a mutex is created for a file, it'll be stored to
 * {@link ConcurrentFileSystem#openFiles} and reused until eviction.<br>
 * Mutex eviction is done by TTL(Time-To-Live) because of actual file sync could
 * be delayed by OS's write-back cache and flushing policy.<br>
 *
 * @author lempel@gmail.com
 * @since 2014. 4. 23.
 */
public class ConcurrentFileSystem extends GenericFileSystem {
    /**
     * maximum evictor interval (10 minutes)
     */
    private static final long MAX_INTERVAL = 10 * 60 * 1000;
    /**
     * minimum evictor interval (5 seconds)
     */
    private static final long MIN_INTERVAL = 5 * 1000;
    /**
     * TTL(Time-To-Live) for {@link ConcurrentFileSystem#openFiles} in
     * milliseconds.<br>
     * Default value is 10 minutes.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static final long EVICTOR_TTL = 10 * 60 * 1000;
    /**
     * Monitor Objects of currently open files (key: path, value: monitor)
     */
    private final Map<String, TimestampedLock> openFiles = new ConcurrentHashMap<>();
    /**
     * lock for openFiles
     */
    private final ReentrantLock openFilesLock = new ReentrantLock();
    /**
     * Periodic evictor thread for openFiles
     */
    // XXX Is there anyway to eliminate EvictorThread?
    @SuppressWarnings("WeakerAccess")
    protected final TerminatableThread evictor = new TerminatableThread() {
        @Override
        public void run() {
            running = true;

            setName("evictor-" + hashCode());

            // set eviction interval
            long interval = EVICTOR_TTL / 2;
            if (interval >= MAX_INTERVAL) {
                interval = MAX_INTERVAL;
            } else if (interval <= MIN_INTERVAL) {
                interval = MIN_INTERVAL;
            }

            while (isValid() && !isTerminated()) {
                try {
                    sleep(interval);
                } catch (InterruptedException ignored) {
                }

                long limit = System.currentTimeMillis() - EVICTOR_TTL;
                openFilesLock.lock();
                try {
                    Set<String> keySet = openFiles.keySet();
                    for (String key : keySet) {
                        TimestampedLock wrapper = openFiles.get(key);
                        // evict timed-out and unlocked mutex
                        if (wrapper.getTimestamp() <= limit && !wrapper.isLocked()) {
                            openFiles.remove(key);
                        }
                    }
                } finally {
                    openFilesLock.unlock();
                }
            }

            terminated = true;
        }
    };

    @SuppressWarnings("WeakerAccess")
    public ConcurrentFileSystem() {
        evictor.setDaemon(true);
        evictor.start();
    }

    /**
     * Gets a lock for specified path
     *
     * @param path file path
     * @return existing lock or new lock
     */
    @SuppressWarnings("WeakerAccess")
    protected TimestampedLock getLock(String path) {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        TimestampedLock result = null;

        openFilesLock.lock();
        try {
            result = openFiles.get(path);
            if (result == null) {
                result = new TimestampedLock(true);
                openFiles.put(path, result);
            }
        } finally {
            openFilesLock.unlock();
        }

        return result;
    }

    // XXX maybe I have to override exists() to check locks too.

    @Override
    public boolean deleteFile(String path) {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        boolean result = false;

        if (exists(path)) {
            TimestampedLock monitor = getLock(path);
            monitor.lock();
            try {
                result = super.deleteFile(path);
            } finally {
                monitor.unlock();
            }
        }

        return result;
    }

    @Override
    public boolean renameFile(String orgPath, String newPath) {
        if (orgPath == null || newPath == null) {
            throw new NullPointerException("at least one of specified path is null");
        }

        boolean result = false;

        if (!orgPath.equals(newPath)) {
            TimestampedLock orgMtx;
            TimestampedLock newMtx;

            openFilesLock.lock();
            try {
                orgMtx = getLock(orgPath);
                newMtx = getLock(newPath);
            } finally {
                openFilesLock.unlock();
            }

            newMtx.lock();
            orgMtx.lock();
            try {
                // can't rename if newPath is currently opened
                result = super.renameFile(orgPath, newPath);
            } finally {
                newMtx.unlock();
                orgMtx.unlock();
            }
        }

        return result;
    }

    @Override
    public byte[] readFile(String path) throws IOException {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        byte[] result = null;

        TimestampedLock monitor = getLock(path);
        monitor.lock();
        try {
            result = super.readFile(path);
        } finally {
            monitor.unlock();
        }

        return result;
    }

    @Override
    public void writeToFile(String path, byte[] contents, boolean append) throws IOException {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        TimestampedLock monitor = getLock(path);
        monitor.lock();
        try {
            super.writeToFile(path, contents, append);
        } finally {
            monitor.unlock();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        evictor.terminate();
    }
}
