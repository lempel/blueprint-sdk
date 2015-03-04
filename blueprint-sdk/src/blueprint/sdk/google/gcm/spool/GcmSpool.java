/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.spool;

import blueprint.sdk.core.concurrent.Worker;
import blueprint.sdk.core.concurrent.WorkerGroup;
import blueprint.sdk.google.gcm.GcmSender;
import blueprint.sdk.util.Terminatable;
import blueprint.sdk.util.queue.H2Queue;
import blueprint.sdk.util.queue.Queue;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.SQLException;

/**
 * Spooler for GCM.<br>
 *
 * @author Sangmin Lee
 * @since 2013. 12. 10.
 */
public class GcmSpool implements Terminatable {
    private static final Logger L = Logger.getLogger(GcmSpool.class);

    private final H2Queue queue;

    private boolean terminated = false;

    private WorkerGroup<String, Queue<String>> workerGroup;

    /**
     * Constructor (uses default error handler and worker)
     *
     * @param datasrc     DataSource for queue
     * @param apiKey      API Key for GCM
     * @param retries     number of retry attempts
     * @param workerCount number of workers
     * @throws SQLException Can't initialize {@link H2Queue} or {@link GcmSpoolWorker}
     */
    @SuppressWarnings("WeakerAccess")
    public GcmSpool(JdbcDataSource datasrc, String apiKey, int retries, int workerCount) throws Exception {
        this(datasrc, apiKey, retries, new GcmErrorHandler(), GcmSpoolWorker.class, workerCount);
    }

    /**
     * Constructor
     *
     * @param datasrc     DataSource for queue
     * @param apiKey      API Key for GCM
     * @param retries     number of retry attempts
     * @param errHandler  error handler
     * @param workerClass Worker class
     * @param workerCount number of workers
     * @throws Exception Can't initialize {@link H2Queue} or {@link GcmSpoolWorker}
     */
    @SuppressWarnings("WeakerAccess")
    public GcmSpool(JdbcDataSource datasrc, String apiKey, int retries, GcmErrorHandler errHandler,
                    Class<? extends Worker<String>> workerClass, int workerCount) throws Exception {
        L.info("Current destination = " + GcmSender.GCM_URL);

        queue = new H2Queue(datasrc);
        queue.init();

        workerGroup = new GcmSpoolWorkerGroup<String, Queue<String>>(queue, workerClass, workerCount, apiKey, retries,
                errHandler);
        workerGroup.start();
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
     * @param json message as json
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

        queue.clear();
    }
}
