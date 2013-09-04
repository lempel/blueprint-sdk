/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.queue;

/**
 * Exception for JdbcQueue
 * 
 * @author Simon Lee
 * @since 2013. 9. 4.
 */
public class JdbcQueueException extends RuntimeException {
	private static final long serialVersionUID = 3168291844624116465L;

	public JdbcQueueException(String message) {
		super(message);
	}

	public JdbcQueueException(String message, Throwable cause) {
		super(message, cause);
	}

	public JdbcQueueException(Throwable cause) {
		super(cause);
	}
}
