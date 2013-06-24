package blueprint.sdk.core.concurrent;

/**
 * non-reentrant mutual exclusion lock
 * 
 * @author Doug Lea
 * @since 1998.6.11.
 */
public class Mutex {
	/** The lock status * */
	private transient boolean inuse = false;

	public void acquire() throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		synchronized (this) {
			try {
				while (inuse) {
					wait();
				}
				inuse = true;
			} catch (InterruptedException ex) {
				notify();
				throw ex;
			}
		}
	}

	public void release() {
		synchronized (this) {
			inuse = false;
			notify();
		}
	}

	public boolean attempt(final long msecs) throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		synchronized (this) {
			if (!inuse) {
				inuse = true;
				return true;
			} else if (msecs <= 0) {
				return false;
			} else {
				long waitTime = msecs;
				long start = System.currentTimeMillis();
				try {
					for (;;) {
						wait(waitTime);
						if (inuse) {
							waitTime = msecs - (System.currentTimeMillis() - start);
							if (waitTime <= 0) {
								return false;
							}
						} else {
							inuse = true;
							return true;
						}
					}
				} catch (InterruptedException ex) {
					notify();
					throw ex;
				}
			}
		}
	}

	/**
	 * returns whether this mutex is in use or not<br>
	 * <b>CAUTION:</b> this method is <b>not thread safe</b><br>
	 * 
	 * @author Simon Lee
	 * @return
	 */
	protected boolean isInuse() {
		return inuse;
	}
}
