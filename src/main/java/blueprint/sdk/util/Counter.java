/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
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
