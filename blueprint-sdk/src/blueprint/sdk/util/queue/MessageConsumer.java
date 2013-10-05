/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.queue;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Consumer for {@link MessageQueue}
 * 
 * @author Sangmin Lee
 * @since 2013. 9. 4.
 */
public class MessageConsumer {
	protected MessageQueue queue;

	private AtomicBoolean blocked = new AtomicBoolean(false);

	public MessageConsumer(MessageQueue queue) {
		this.queue = queue;
	}

	/**
	 * Receives an element from queue.<br>
	 * Blocks until queue is not empty of consumer is interrupted.<br>
	 * 
	 * @return element from queue or null(consumer is interrupted)
	 * @throws SQLException
	 *             Can't delete from queue's storage
	 */
	public String receive() throws SQLException {
		String result = null;

		result = queue.pop();
		if (result == null) {
			queue.waiters.push(this);
			synchronized (this) {
				blocked.set(true);

				// Can't be interrupted by others
				while (blocked.get()) {
					try {
						wait();
						result = queue.pop();
					} catch (InterruptedException ignored) {
					}
				}
			}
		}

		return result;
	}

	/**
	 * Interrupt blocking status
	 */
	public void interrupt() {
		synchronized (this) {
			if (blocked.get()) {
				blocked.set(false);
				notify();
			}
		}
	}
}
