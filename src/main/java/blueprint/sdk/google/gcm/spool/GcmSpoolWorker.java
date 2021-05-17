/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.google.gcm.spool;

import blueprint.sdk.core.concurrent.Worker;
import blueprint.sdk.google.gcm.GcmResponse;
import blueprint.sdk.google.gcm.GcmSender;
import blueprint.sdk.util.queue.Queue;

import java.io.IOException;

/**
 * Worker for GcmSpool.
 *
 * @author lempel@gmail.com
 * @since 2013. 12. 11.
 * @deprecated GCM is replaced by FCM - https://firebase.google.com/docs/cloud-messaging
 */
public class GcmSpoolWorker extends Worker<String> {
    private final GcmSender sender;
    private final int retries;
    private final GcmErrorHandler errHandler;

    /**
     * Constructor
     *
     * @param jobQueue     job queue to use
     * @param deathMonitor notify this monitor after termination
     * @param apiKey       API Key for GCM
     * @param retries      number of retry attempts
     * @param errHandler   error handler
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
