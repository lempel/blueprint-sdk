/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.filesystem;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import blueprint.sdk.core.concurrent.lock.StampedMutex;
import blueprint.sdk.util.jvm.shutdown.TerminatableThread;

/**
 * Thread Safe File System.<br/>
 * <br/>
 * For each and every file, a {@link StampedMutex} is created for
 * synchonization.<br/>
 * Once a mutex is created for a file, it'll be stored to
 * {@link ConcurrentFileSystem#openFiles} and reused until eviction.<br/>
 * Mutex eviction is done by TTL(Time-To-Live) because of actual file sync could
 * be delayed by OS's write-back cache & flushing policy.<br/>
 * 
 * @author Sangmin Lee
 * @since 2014. 4. 23.
 */
public class ConcurrentFileSystem extends GenericFileSystem {
	/** Monitor Objects of currently open files (key: path, value: monitor) */
	protected Map<String, StampedMutex> openFiles = new ConcurrentHashMap<String, StampedMutex>();

	/**
	 * TTL(Time-To-Live) for {@link ConcurrentFileSystem#openFiles} in
	 * millisecounds.<br/>
	 * Default value is 10 minutes.
	 */
	protected static long EVICTOR_TTL = 10 * 60 * 1000;

	/** maximum evictor interval (10 minutes) */
	protected static final long MAX_INTERVAL = 10 * 60 * 1000;
	/** minimun evictor interval (5 seconds) */
	protected static final long MIN_INTERVAL = 5 * 1000;

	/**
	 * Peroidic evictor thread for openFiles
	 */
	protected TerminatableThread evictor = new TerminatableThread() {
		@Override
		public void run() {
			running = true;

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
						StampedMutex wrapper = openFiles.get(key);
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

	/** lock for openFiles */
	protected ReentrantLock openFilesLock = new ReentrantLock();

	public ConcurrentFileSystem() {
		evictor.start();
	}

	/**
	 * Gets a lock for specified path
	 * 
	 * @param path
	 *            file path
	 * @return existing lock or new lock
	 */
	protected StampedMutex getLock(String path) {
		if (path == null) {
			throw new NullPointerException("specified path is null");
		}

		StampedMutex result = null;

		openFilesLock.lock();
		try {
			result = openFiles.get(path);
			if (result == null) {
				result = new StampedMutex();
				openFiles.put(path, result);
			}
		} finally {
			openFilesLock.unlock();
		}

		return result;
	}

	@Override
	public boolean deleteFile(String path) {
		if (path == null) {
			throw new NullPointerException("specified path is null");
		}

		boolean result = false;

		if (exists(path)) {
			StampedMutex monitor = getLock(path);
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
			StampedMutex orgMtx;
			StampedMutex newMtx;

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

		StampedMutex monitor = getLock(path);
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

		StampedMutex monitor = getLock(path);
		monitor.lock();
		try {
			super.writeToFile(path, contents, append);
		} catch (IOException e) {
			throw e;
		} finally {
			monitor.unlock();
		}
	}
}
