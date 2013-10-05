/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent;

import java.lang.reflect.InvocationTargetException;

/**
 * A Group of Workers<br>
 * Maintains JobQueue and Workers<br>
 * <br>
 * Provides automatic thread spanning feature, call start() method.<br>
 * 
 * @author Sangmin Lee
 * @since 2008. 11. 25.
 */
public class SpanningWorkerGroup extends WorkerGroup {
	/** check interval (msec) */
	private static final int INTERVAL = 10000;
	/** worker thread increase ratio */
	private static final float THREAD_INC_RATIO = 0.2f;

	private transient boolean running = false;
	private transient boolean terminated = false;

	private long maxThroughput = 0;

	/**
	 * Constructor<br>
	 * Creates Workers and JobQueue<br>
	 * 
	 * @param workerClass
	 * @param workerCount
	 *            Initial number of workers
	 * @throws InvocationTargetException
	 *             Worker instantiation failure
	 * @throws IllegalAccessException
	 *             Can't access Worker's constructor (it should not happen)
	 * @throws InstantiationException
	 *             Worker instantiation failure
	 * @throws IllegalArgumentException
	 *             Wrong argument for Worker's constructor (it should not
	 *             happen)
	 * @throws NoSuchMethodException
	 *             workerClass is not an Worker or has no visible constructor
	 * @throws SecurityException
	 *             Can't retrieve Worker's constructor
	 */
	public SpanningWorkerGroup(final Class<? extends Worker<?>> workerClass, final int workerCount)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException {
		super(workerClass, workerCount);
	}

	public boolean isValid() {
		return running;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() {
		running = false;

		super.terminate();
	}

	public void run() {
		running = true;
		boolean interrupted = false;
		long start = 0L;

		jobQueue.setCount(true);

		while (running) {
			try {
				// reset interrupted flag & keep start time
				interrupted = false;
				// new period, update start time
				start = System.currentTimeMillis();

				Thread.sleep(INTERVAL);
			} catch (InterruptedException ignored) {
				interrupted = true;
			}

			if (!interrupted) {
				maintainWorkers();

				// calculate elapsed time
				long elapsed = System.currentTimeMillis() - start;

				spanWorkers(elapsed);
			}
		}

		terminated = true;
	}

	/**
	 * @param elapsed
	 *            elapsed time in msec
	 */
	private void spanWorkers(long elapsed) {
		// convert msec to sec with rounding
		elapsed = (elapsed + 500) / 1000;

		// calculate throughput & reset counter
		long throughtput = jobQueue.getProcessedJobs() / elapsed;
		jobQueue.resetProcessedJobs();

		// is all busy situation occured?
		if (jobQueue.isAllBusyTrapped()) {
			jobQueue.resetAllBusyTrap();

			int newThreads = (int) (THREAD_INC_RATIO * workers.size());
			if (throughtput >= maxThroughput) {
				maxThroughput = throughtput;

				// increase the number of threads
				addWorkers(newThreads);
			} else {
				// decrease the number of threads
				removeWorkers(newThreads);
			}
		}
	}
}
