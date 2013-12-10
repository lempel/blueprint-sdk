/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent;

import org.apache.log4j.Logger;

import blueprint.sdk.util.jvm.shutdown.TerminatableThread;

/**
 * A Group of Workers<br>
 * Maintains JobQueue and Workers<br>
 * <br>
 * Provides automatic thread spanning feature, call start() method.<br>
 * 
 * @author Sangmin Lee
 * @since 2008. 11. 25.
 */
public class SpanningWorkerGroup<J, Q extends JobQueue<J>> extends WorkerGroup<J, JobQueue<J>> {
	private static final Logger L = Logger.getLogger(SpanningWorkerGroup.class);

	/** check interval (msec) */
	protected static final int INTERVAL = 1000;

	private long maxThroughput = 0;

	protected Refiller refiller;

	/**
	 * Constructor<br>
	 * Creates Workers and JobQueue<br>
	 * 
	 * @param jobQueue
	 * @param workerClass
	 * @param workerCount
	 *            Initial number of workers
	 * @throws Exception
	 *             Can't create workers
	 */
	public SpanningWorkerGroup(final Q jobQueue, final Class<? extends Worker<J>> workerClass, final int workerCount)
			throws Exception {
		super(jobQueue, workerClass, workerCount);

		refiller = new Refiller();
		refiller.start();
	}

	public void run() {
		try {
			// instantiate & start workers
			for (int i = 0; i < initialWorkers; i++) {
				newWorker();
			}

			running = true;

			// if initial number of workers are too small, increase it first
			if (workers.size() * THREAD_INC_RATIO < 1) {
				int newThreads = (int) (1.0f / THREAD_INC_RATIO) - workers.size();
				addWorkers(newThreads);
			}
		} catch (Exception e) {
			L.error("Can't create workers. Terminating " + getClass().getSimpleName(), e);
		}

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

	/**
	 * Refills dead workers
	 * 
	 * @author Sangmin Lee
	 * @since 2013. 12. 11.
	 */
	class Refiller extends TerminatableThread {
		@Override
		public void run() {
			running = true;
			while (running) {
				synchronized (deathMonitor) {
					try {
						deathMonitor.wait();
					} catch (InterruptedException ignored) {
					}

					maintainWorkers();
				}
			}
			terminated = true;
		}
	}
}
