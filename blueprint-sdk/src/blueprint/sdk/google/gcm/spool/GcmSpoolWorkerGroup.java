/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.spool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import blueprint.sdk.core.concurrent.Worker;
import blueprint.sdk.core.concurrent.WorkerGroup;
import blueprint.sdk.util.queue.Queue;

/**
 * WorkerGroup for GcmSpool
 * 
 * @param <J>
 *            Job Type
 * @param <Q>
 *            Queue Type
 * @author Sangmin Lee
 * @since 2013. 12. 11.
 */
public class GcmSpoolWorkerGroup<J, Q> extends WorkerGroup<J, Queue<J>> {
	/**
	 * Constructor
	 * 
	 * @param jobQueue
	 * @param workerClass
	 * @param workerCount
	 *            Initial number of workers
	 */
	public GcmSpoolWorkerGroup(Queue<J> jobQueue, Class<? extends Worker<J>> workerClass, int workerCount) {
		super(jobQueue, workerClass, workerCount);
	}

	@Override
	protected void newWorker() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Worker<J> aWorker;
		Constructor<? extends Worker<J>> cons = workerClass.getConstructor(Queue.class, Object.class, String.class,
				int.class, GcmErrorHandler.class);
		aWorker = cons.newInstance(jobQueue);
		workers.add(aWorker);
		aWorker.start();
	}
}