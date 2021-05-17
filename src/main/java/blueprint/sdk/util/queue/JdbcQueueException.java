/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.queue;

/**
 * Exception for JdbcQueue
 *
 * @author lempel@gmail.com
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
