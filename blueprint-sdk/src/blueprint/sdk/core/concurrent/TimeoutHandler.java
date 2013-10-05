/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import blueprint.sdk.util.Terminatable;

/**
 * Checks registered Terminatables and terminates timed-out or invalid ones
 * 
 * @author Sangmin Lee
 * @since 2007. 07. 20
 */
public final class TimeoutHandler implements Terminatable, Runnable {
	private static final Logger L = Logger.getLogger(WorkerGroup.class);

	private transient Hashtable<Terminatable, Long> map = new Hashtable<Terminatable, Long>();
	private transient boolean running = false;
	private transient boolean terminated = false;

	/** timeout (msec) */
	private final int timeout;
	/** check interval (msec) */
	private final int interval;

	private TimeoutHandler(final int timeout, final int interval) {
		L.info("creating timeout handler - timeout: " + timeout + "s, interval: " + timeout + "s");

		this.timeout = timeout * 1000;
		this.interval = interval * 1000;

		L.info("timeout handler created - timeout: " + timeout + "s, _timeout: " + interval + "s");
	}

	public static TimeoutHandler newTimeoutHandler(final int timeout, final int interval) {
		TimeoutHandler result = new TimeoutHandler(timeout, interval);
		result.start();
		return result;
	}

	/**
	 * Updates given Terminatable's timestamp<br>
	 * Call right after every SUCCESSFUL read/write<br>
	 * 
	 * @param con
	 */
	public void updateTimestamp(Terminatable con) {
		map.put(con, System.currentTimeMillis());

		L.debug("updateTimestamp - <" + con + ", " + map.get(con) + ">");
	}

	/**
	 * removes given Terminatable from handler
	 * 
	 * @param con
	 */
	public void remove(Terminatable con) {
		map.remove(con);
	}

	public void start() {
		Thread thr = new Thread(this);
		thr.setName(this.getClass().getName());
		thr.setDaemon(true);
		thr.start();
	}

	public boolean isValid() {
		return running;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() {
		running = false;

		Enumeration<Terminatable> enu = map.keys();
		while (enu.hasMoreElements()) {
			enu.nextElement().terminate();
		}
		map.clear();
	}

	public void run() {
		running = true;

		L.info("timeout handler started - timeout: " + timeout + "ms, interval: " + interval + "ms");
		while (running) {
			try {
				Thread.sleep(getInterval());
			} catch (InterruptedException ignored) {
				L.error("oh my fuckin got!");
			}

			long currentTime = System.currentTimeMillis();

			Enumeration<Terminatable> enu = map.keys();
			long count = 0;
			while (enu.hasMoreElements()) {
				Terminatable key = (Terminatable) enu.nextElement();
				if (key.isValid()) {
					long timestamp = map.get(key);

					// close & cancel timed-out keys
					if (currentTime - timestamp >= getTimeout()) {
						key.terminate();
						count++;
					}
				} else {
					key.terminate();
					map.remove(key);
				}
			}

			if (count > 0) {
				L.debug(count + " connections are timed out (" + map.size() + " connections)");
			}
		}

		terminated = true;

		L.info("timeout handler stopped - timeout: " + timeout + " interval: " + interval);
	}

	public boolean isRunning() {
		return running;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getInterval() {
		return interval;
	}

	@Override
	protected void finalize() throws Throwable {
		map.clear();
		map = null;

		super.finalize();
	}
}