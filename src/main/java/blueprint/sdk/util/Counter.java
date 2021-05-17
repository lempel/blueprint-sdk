/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util;

/**
 * Thread-Safe Counter
 *
 * @author lempel@gmail.com
 * @since 2009. 2. 26.
 */
public class Counter {
    private long total = 0;

    public void increase() {
        synchronized (this) {
            total++;
        }
    }

    public void decrease() {
        synchronized (this) {
            total--;
        }
    }

    public void reset() {
        synchronized (this) {
            total = 0;
        }
    }

    public long count() {
        synchronized (this) {
            return total;
        }
    }
}
