/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.spool;

import javax.sql.DataSource;

import blueprint.sdk.core.concurrent.Worker;
import blueprint.sdk.core.concurrent.WorkerGroup;
import blueprint.sdk.util.Terminatable;
import blueprint.sdk.util.queue.H2Queue;
import blueprint.sdk.util.queue.Queue;

/**
 * Spooler for GCM.<br>
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 10.
 */
public class GcmSpool implements Terminatable {
	protected H2Queue queue;

	protected boolean terminated = false;

	protected GcmErrorHandler errHandler;

	protected WorkerGroup<String, Queue<String>> workerGroup;

	/**
	 * Constructor (uses default error handler and worker)
	 * 
	 * @param datasrc
	 *            DataSource for queue
	 * @param workerCount
	 */
	public GcmSpool(DataSource datasrc, int workerCount) {
		this(datasrc, new GcmErrorHandler(), GcmSpoolWorker.class, workerCount);
	}

	/**
	 * Constructor
	 * 
	 * @param datasrc
	 *            DataSource for queue
	 * @param errHandler
	 * @param workerClass
	 * @param workerCount
	 */
	public GcmSpool(DataSource datasrc, GcmErrorHandler errHandler, Class<? extends Worker<String>> workerClass,
			int workerCount) {
		queue = new H2Queue(datasrc);

		this.errHandler = errHandler;
		workerGroup = new GcmSpoolWorkerGroup<String, Queue<String>>(queue, workerClass, workerCount);
	}

	@Override
	public boolean isValid() {
		return !terminated;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * add a message to send
	 * 
	 * @param json
	 *            message as json
	 */
	public void addMessage(String json) {
		queue.push(json);
	}

	@Override
	public void terminate() {
		terminated = true;

		if (workerGroup != null) {
			workerGroup.terminate();
		}
	}
}
