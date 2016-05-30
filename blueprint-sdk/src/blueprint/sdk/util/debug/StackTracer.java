/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints current thread's stack trace at anytime..
 *
 * @author lempel@gmail.com
 * @since 2013. 9. 5.
 */
@SuppressWarnings("WeakerAccess")
public class StackTracer {
    private static final Logger L = LoggerFactory.getLogger(StackTracer.class);

    /**
     * Prints current stack trace
     */
    public static void printStackTrace() {
        try {
            throw new Exception("printStackTrace");
        } catch (Exception e) {
            L.info("printStackTrace", e);
        }
    }
}
