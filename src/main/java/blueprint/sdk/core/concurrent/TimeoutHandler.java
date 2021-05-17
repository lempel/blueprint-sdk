/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.core.concurrent;

import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blueprint.sdk.util.Terminatable;

/**
 * Checks registered Terminatables and terminates timed-out or invalid ones
 *
 * @author lempel@gmail.com
 * @since 2007. 07. 20
 */
public final class TimeoutHandler implements Terminatable, Runnable {
    private static final Logger L = LoggerFactory.getLogger(WorkerGroup.class);
    /**
     * timeout (msec)
     */
    private final int timeout;
    /**
     * check interval (msec)
     */
    private final int interval;
    private transient Hashtable<Terminatable, Long> map = new Hashtable<>();
    private transient boolean running = false;
    private transient boolean terminated = false;

    private TimeoutHandler(final int timeout, final int interval) {
        L.info("creating timeout handler - timeout: " + timeout + "s, interval: " + timeout + "s");

        this.timeout = timeout * 1000;
        this.interval = interval * 1000;

        L.info("timeout handler created - timeout: " + timeout + "s, _timeout: " + interval + "s");
    }

    public static TimeoutHandler newTimeoutHandler(final int timeout, final int interval) {
        TimeoutHandler result = new TimeoutHandler(timeout, interval);
        result.start();
        return result;
    }

    /**
     * Updates given Terminatable's timestamp<br>
     * Call right after every SUCCESSFUL read/write<br>
     *
     * @param target target to update
     */
    public void updateTimestamp(Terminatable target) {
        map.put(target, System.currentTimeMillis());

        L.debug("updateTimestamp - <" + target + ", " + map.get(target) + ">");
    }

    /**
     * removes given Terminatable from handler
     *
     * @param con target to remove
     */
    public void remove(Terminatable con) {
        map.remove(con);
    }

    @SuppressWarnings("WeakerAccess")
    public void start() {
        Thread thr = new Thread(this);
        thr.setName(this.getClass().getName());
        thr.setDaemon(true);
        thr.start();
    }

    public boolean isValid() {
        return running;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void terminate() {
        running = false;

        Enumeration<Terminatable> enu = map.keys();
        while (enu.hasMoreElements()) {
            enu.nextElement().terminate();
        }
        map.clear();
    }

    public void run() {
        running = true;

        L.info("timeout handler started - timeout: " + timeout + "ms, interval: " + interval + "ms");
        while (running) {
            try {
                Thread.sleep(getInterval());
            } catch (InterruptedException ignored) {
                L.error("oh my fuckin got!");
            }

            long currentTime = System.currentTimeMillis();

            Enumeration<Terminatable> enu = map.keys();
            long count = 0;
            while (enu.hasMoreElements()) {
                Terminatable key = enu.nextElement();
                if (key.isValid()) {
                    long timestamp = map.get(key);

                    // close & cancel timed-out keys
                    if (currentTime - timestamp >= getTimeout()) {
                        key.terminate();
                        count++;
                    }
                } else {
                    key.terminate();
                    map.remove(key);
                }
            }

            if (count > 0) {
                L.debug(count + " connections are timed out (" + map.size() + " connections)");
            }
        }

        terminated = true;

        L.info("timeout handler stopped - timeout: " + timeout + " interval: " + interval);
    }

    public boolean isRunning() {
        return running;
    }

    @SuppressWarnings("WeakerAccess")
    public int getTimeout() {
        return timeout;
    }

    @SuppressWarnings("WeakerAccess")
    public int getInterval() {
        return interval;
    }

    @Override
    protected void finalize() throws Throwable {
        map.clear();
        map = null;

        super.finalize();
    }
}