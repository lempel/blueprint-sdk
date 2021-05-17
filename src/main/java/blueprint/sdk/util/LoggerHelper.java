/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

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
