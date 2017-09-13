package blueprint.sdk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger Helper
 *
 * @author lempel@gmail.com
 * @since 2016-11-22
 */
public class LoggerHelper {
    public static Logger get() {
        Logger result = null;

        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length > 2) {
            result = LoggerFactory.getLogger(elements[2].getClassName());
        }

        return result;
    }
}
