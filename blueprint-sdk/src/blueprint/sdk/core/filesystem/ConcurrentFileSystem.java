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
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread Safe File System
 * 
 * @author Sangmin Lee
 * @since 2014. 4. 23.
 */
public class ConcurrentFileSystem extends GenericFileSystem {
	/** Monitor Objects of currently open files (key: path, value: monitor) */
	protected HashMap<String, Object> openFiles = new HashMap<String, Object>();

	/** lock for openFiles */
	protected ReentrantLock openFilesLock = new ReentrantLock();

	/**
	 * Gets a Monitor Object for specified path
	 * 
	 * @param path
	 *            file path
	 * @return Monitor Object
	 */
	protected Object getMonitor(String path) {
		openFilesLock.lock();
		try {
			return openFiles.get(path);
		} finally {
			openFilesLock.unlock();
		}
	}

	/**
	 * Gets a Monitor Object for specified path
	 * 
	 * @param path
	 *            file path
	 * @return Monitor Object
	 */
	protected Object newMonitor(String path) {
		Object result = null;

		openFilesLock.lock();
		try {
			result = openFiles.get(path);
			if (result == null) {
				result = new Object();
				openFiles.put(path, result);
			}
		} finally {
			openFilesLock.unlock();
		}

		return result;
	}

	/**
	 * Releases Monitor Object for specified path if exists
	 * 
	 * @param path
	 *            file path
	 */
	protected void releaseMonitor(String path) {
		openFilesLock.lock();
		try {
			Object monitor = openFiles.get(path);

			if (monitor != null) {
				openFiles.remove(path);
			}
		} finally {
			openFilesLock.unlock();
		}
	}

	@Override
	public boolean deleteFile(String path) {
		boolean result = false;

		if (exists(path)) {
			Object monitor = getMonitor(path);
			if (monitor == null) {
				result = super.deleteFile(path);
			} else {
				synchronized (monitor) {
					result = super.deleteFile(path);
				}
			}
		}

		return result;
	}

	@Override
	public boolean renameFile(String orgPath, String newPath) {
		boolean result = false;
		Object orgMonitor;
		Object newMonitor;

		openFilesLock.lock();
		try {
			orgMonitor = newMonitor(orgPath);
			newMonitor = getMonitor(newPath);
		} finally {
			openFilesLock.unlock();
		}

		// can't rename if newPath is currently opened
		if (newMonitor == null) {
			synchronized (orgMonitor) {
				try {
					result = super.renameFile(orgPath, newPath);
				} finally {
					releaseMonitor(orgPath);
				}
			}
		}

		return result;
	}

	@Override
	public byte[] readFile(String path) throws IOException {
		byte[] result = null;

		Object monitor = newMonitor(path);
		synchronized (monitor) {
			try {
				result = super.readFile(path);
			} finally {
				releaseMonitor(path);
			}
		}

		return result;
	}

	@Override
	public void writeToFile(String path, byte[] contents, boolean append) throws IOException {
		Object monitor = newMonitor(path);

		synchronized (monitor) {
			try {
				super.writeToFile(path, contents, append);
			} finally {
				releaseMonitor(path);
			}
		}
	}
}
