/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.core.concurrent;

import blueprint.sdk.util.LoggerHelper;
import blueprint.sdk.util.Terminatable;
import blueprint.sdk.util.Validator;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Manages {@link Consumer} for deferred execution.<br>
 * <br>
 * <b>Evicted consumers will be fired with null argument.</b><br>
 *
 * @author lempel@gmail.com
 * @since 2019. 3. 4.
 */
public class DeferredConsumers<T> implements Terminatable, Runnable {
    private static final Logger L = LoggerHelper.get();

    /**
     * Wraps {@link Consumer} with timestamp
     */
    private class Wrapper {
        String key;
        long stamp;
        Consumer<T> consumer;
    }

    private Map<String, Wrapper> map = new ConcurrentHashMap<>();

    private long interval;
    private long ttl;
    private boolean running = false;

    /**
     * Constructor
     *
     * @param interval interval of eviction process (msec)
     * @param ttl      time to live (msec)
     */
    public DeferredConsumers(long interval, long ttl) {
        this.interval = interval;
        this.ttl = ttl;
    }

    /**
     * Start monitoring
     */
    public void start() {
        Thread t = new Thread(this);
        t.setDaemon(true);

        Runtime.getRuntime().addShutdownHook(t);
        t.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                long now = System.currentTimeMillis();

                List<Wrapper> candidates = map.values().stream()
                        .filter(w -> w.stamp + ttl >= now)
                        .collect(Collectors.toList());

                candidates.forEach(w -> {
                    map.remove(w.key);
                    if (w.consumer != null) {
                        w.consumer.accept(null);
                    }
                });
            } catch (Exception e) {
                L.warn("Failed to evict stale consumers", e);
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Puts a {@link CountDownLatch} for deferred count down
     *
     * @param key      some key
     * @param consumer consumer
     */
    public void put(String key, Consumer<T> consumer) {
        if (!Validator.isEmpty(key)) {
            Wrapper w = new Wrapper();
            w.key = key;
            w.stamp = System.currentTimeMillis();
            w.consumer = consumer;

            map.put(key, w);
        }
    }

    /**
     * Fires a consumer which is associated with given key
     *
     * @param key some key
     * @param arg argument for {@link Consumer}
     */
    public void fire(String key, T arg) {
        if (!Validator.isEmpty(key)) {
            Wrapper w = map.get(key);
            if (w != null && w.consumer != null) {
                w.consumer.accept(arg);
            }
        }
    }

    @Override
    public boolean isValid() {
        return running;
    }

    @Override
    public boolean isTerminated() {
        return running;
    }

    @Override
    public void terminate() {
        running = false;
        this.notifyAll();
    }
}
