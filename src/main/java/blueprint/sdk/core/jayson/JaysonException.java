/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.core.jayson;

/**
 * A {@link RuntimeException} that indicates internal Jayson exception
 *
 * @author lempel@gmail.com
 * @since 2021. 5. 2.
 */
public class JaysonException extends RuntimeException {
    public JaysonException() {
    }

    public JaysonException(String message) {
        super(message);
    }

    public JaysonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaysonException(Throwable cause) {
        super(cause);
    }

    public JaysonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
