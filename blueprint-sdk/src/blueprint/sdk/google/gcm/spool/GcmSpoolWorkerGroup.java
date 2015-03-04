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
import blueprint.sdk.util.queue.Queue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * WorkerGroup for GcmSpool
 *
 * @param <J> Job Type
 * @param <Q> Queue Type
 * @author Sangmin Lee
 * @since 2013. 12. 11.
 */
public class GcmSpoolWorkerGroup<J, Q> extends WorkerGroup<J, Queue<J>> {
    /**
     * API Key for GCM
     */
    private final String apiKey;
    /**
     * number of retry attempts
     */
    private final int retries;
    /**
     * error handler
     */
    private final GcmErrorHandler errHandler;

    /**
     * Constructor
     *
     * @param jobQueue    job queue
     * @param workerClass Worker class
     * @param workerCount Initial number of workers
     * @param apiKey      API Key for GCM
     * @param errHandler  error handler
     * @param retries     number of retry attempts
     */
    public GcmSpoolWorkerGroup(Queue<J> jobQueue, Class<? extends Worker<J>> workerClass, int workerCount,
                               String apiKey, int retries, GcmErrorHandler errHandler) {
        super(jobQueue, workerClass, workerCount);

        this.apiKey = apiKey;
        this.retries = retries;
        this.errHandler = errHandler;
    }

    @Override
    protected void newWorker() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Worker<J> aWorker;
        Constructor<? extends Worker<J>> cons = workerClass.getConstructor(Queue.class, Object.class, String.class,
                int.class, GcmErrorHandler.class);
        aWorker = cons.newInstance(jobQueue, deathMonitor, apiKey, retries, errHandler);
        workers.add(aWorker);
        aWorker.start();
    }
}