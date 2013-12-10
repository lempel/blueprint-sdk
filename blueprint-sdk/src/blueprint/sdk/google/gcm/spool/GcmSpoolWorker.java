/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.spool;

import java.io.IOException;

import blueprint.sdk.core.concurrent.Worker;
import blueprint.sdk.google.gcm.GcmResponse;
import blueprint.sdk.google.gcm.GcmSender;
import blueprint.sdk.util.queue.Queue;

/**
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 11.
 */
public class GcmSpoolWorker extends Worker<String> {
	protected GcmSender sender;
	protected int retries;
	protected GcmErrorHandler errHandler;

	/**
	 * Constructor
	 * 
	 * @param jobQueue
	 * @param deathMonitor
	 * @param apiKey
	 *            API Key for GCM
	 * @param retries
	 *            number of retry attempts
	 * @param errHandler
	 *            error handler
	 */
	public GcmSpoolWorker(Queue<String> jobQueue, Object deathMonitor, String apiKey, int retries,
			GcmErrorHandler errHandler) {
		super(jobQueue, deathMonitor);

		sender = new GcmSender(apiKey);
		this.retries = retries;
		this.errHandler = errHandler;
	}

	@Override
	protected void process(String job) {
		try {
			GcmResponse response = sender.send(job, retries);

			errHandler.handlerGcmError(job, response);
		} catch (IOException e) {
			errHandler.handleIoError(job, e);
		}
	}
}
