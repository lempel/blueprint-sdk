/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import org.apache.log4j.Logger;

import blueprint.sdk.util.Terminatable;

/**
 * Monitors memory usage<br>
 * For better result, please set -Xmx argument.<br>
 * 
 * @author Simon Lee
 * @since 2009. 3. 2.
 */
public class MemoryMonitor implements Terminatable, Runnable {
	private static final Logger L = Logger.getLogger(MemoryMonitor.class);

	/** check interval - 10sec */
	private static final int INTERVAL = 10000;
	/** memory usage limit to warn - 80% */
	private static final int WARNING_USAGE = 80;
	/** maximum tolerable warnings - 6times */
	private static final int MAX_WARNINGS = 6;

	private boolean running = false;
	private transient boolean terminated = false;
	private boolean trace = false;

	public MemoryMonitor() {
		super();
	}

	public MemoryMonitor(boolean trace) {
		super();

		this.trace = trace;
	}

	public boolean isValid() {
		return running;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() {
		running = false;
	}

	public static int getMemoryUsage() {
		Runtime rtime = Runtime.getRuntime();
		long total = rtime.maxMemory();
		long used = rtime.totalMemory() - rtime.freeMemory();
		double ratio = (double) used / (double) total;
		return (int) (ratio * 100d);
	}

	public static boolean isXmxSet() {
		boolean result = false;

		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = RuntimemxBean.getInputArguments();
		for (String arg : arguments) {
			if (arg.toLowerCase().startsWith("-xmx")) {
				result = true;
				break;
			}
		}

		return result;
	}

	public void start() {
		Thread thr = new Thread(this);
		thr.setName(this.getClass().getName());
		thr.setDaemon(true);
		thr.start();
	}

	public void run() {
		running = true;

		boolean interrupted = false;
		int warnCount = 0;
		boolean xmx = isXmxSet();

		while (running) {
			try {
				if (!interrupted) {
					Runtime rtime = Runtime.getRuntime();
					long total = rtime.maxMemory();
					long used = rtime.totalMemory() - rtime.freeMemory();
					double ratio = (double) used / (double) total;
					int percent = (int) (ratio * 100d);

					if (percent >= WARNING_USAGE) {
						warnCount++;
						if (warnCount >= MAX_WARNINGS) {
							L.error("LOW FREE MEMORY!! Memory usage is Critical. Over " + WARNING_USAGE
									+ "% for long time.");
							if (xmx) {
								L.error("RECOMMEND: 1. Increase -Xmx value");
							} else {
								L.error("RECOMMEND: 1. Set -Xmx value");
							}
							L.error("RECOMMEND: 2. Check for memory leak");
							L.error("RECOMMEND: 3. Increase performance or use better machine)");
							warnCount = 0;
						} else {
							L.warn("Memory usage: " + percent + "% - " + (used / 1024 / 1024) + "M");
						}
					} else {
						warnCount = 0;
					}

					if (trace) {
						L.info("Memory usage: " + percent + "% - " + (used / 1024 / 1024) + "M");
					}
				}
			} catch (OutOfMemoryError oom) {
				L.error("OutOfMemoryError", oom);
			}

			try {
				Thread.sleep(INTERVAL);
				interrupted = false;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}

		terminated = true;
	}
}
