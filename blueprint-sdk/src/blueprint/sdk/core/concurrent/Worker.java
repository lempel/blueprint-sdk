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

import blueprint.sdk.util.Terminatable;

/**
 * Worker Thread
 * 
 * @param <T> job class
 * @author Sangmin Lee
 * @since 2007. 07. 25
 */
public abstract class Worker<T> implements Terminatable, Runnable {
	private static final Logger LOGGER = Logger.getLogger(WorkerGroup.class);

	protected transient JobQueue<T> jobQueue = null;

	private transient boolean running = false;

	private transient boolean terminated = false;

	private transient boolean active = false;

	public Worker(final JobQueue<T> jobQueue) {
		this.jobQueue = jobQueue;
	}

	public void start() {
		Thread thr = new Thread(this);
		thr.setName(this.getClass().getName());
		thr.setDaemon(true);
		thr.start();
	}

	public void run() {
		running = true;

		while (running) {
			try {
				// blocks until queue have something to pop
				T job = jobQueue.pop();
				active = true;
				process(job);
			} catch (InterruptedException e) {
				LOGGER.error(e.toString());
			} finally {
				jobQueue.increaseProcessedJobCounter();
				active = false;
			}
		}

		terminated = true;
	}

	/**
	 * handles a job or client object
	 * 
	 * @param clientObject
	 */
	protected abstract void process(T clientObject);

	public boolean isRunning() {
		return running;
	}

	public boolean isValid() {
		return isRunning();
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() {
		running = false;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	protected void finalize() throws Throwable {
		jobQueue.clear();
		jobQueue = null;

		super.finalize();
	}
}