/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.cache;

import blueprint.sdk.util.Validator;
import blueprint.sdk.util.jvm.shutdown.TerminatableThread;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very simple Object cache with lazy eviction.
 *
 * @param <T> element type
 * @author lempel@gmail.com
 * @since 2013. 6. 28.
 */
public class Cache<T> {
    /**
     * actual cache
     */
    private final Map<String, CacheItem<T>> cache;

    /**
     * time to live (in milli-seconds, 0 = no eviction)
     */
    private final long timeToLive;

    /**
     * active evictor
     */
    private CacheEvictor evictor;

    /**
     * Create an instance of Cache with very lazy eviction policy.<br>
     * Expired items will be evicted by {@link Cache#get(String)}.<br>
     *
     * @param timeToLive time to live (in seconds, 0 = no eviction)
     */
    public Cache(long timeToLive) {
        this(5, timeToLive);
    }

    /**
     * Create an instance of Cache with very lazy eviction policy.<br>
     * Expired items will be evicted by {@link Cache#get(String)}.<br>
     *
     * @param initialSize initial size of cache
     * @param timeToLive  time to live (in seconds, 0 = no eviction)
     */
    @SuppressWarnings("WeakerAccess")
    public Cache(int initialSize, long timeToLive) {
        super();

        cache = new ConcurrentHashMap<>(initialSize);

        this.timeToLive = timeToLive * 1000;
    }

    /**
     * Create an instance of Cache with very lazy eviction policy.<br>
     * Evictor thread would periodically evict expired items.<br>
     *
     * @param initialSize      initial size of cache
     * @param timeToLive       time to live (in seconds, 0 = no eviction)
     * @param useActiveEvictor true: create an evictor thread
     */
    @SuppressWarnings("SameParameterValue")
    public Cache(int initialSize, long timeToLive, boolean useActiveEvictor) {
        this(initialSize, timeToLive);

        if (useActiveEvictor) {
            evictor = new CacheEvictor(cache, timeToLive);
            evictor.start();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected static boolean isAlive(CacheItem<?> item, long timeToLive) {
        boolean result = false;

        if (item != null && item.timestamp >= (System.currentTimeMillis() - timeToLive)) {
            result = true;
        }

        return result;
    }

    /**
     * get an element
     *
     * @param key key of element
     * @return cache element
     */
    public T get(String key) {
        T result = null;

        if (!Validator.isEmpty(key)) {
            CacheItem<T> item = cache.get(key);
            if (item != null) {
                if (isAlive(item)) {
                    result = item.element;
                } else {
                    cache.remove(key);
                }
            }
        }

        return result;
    }

    /**
     * put an element
     *
     * @param key key of element
     * @param element element to put
     * @return previous element
     */
    @SuppressWarnings("UnusedReturnValue")
    public T put(String key, T element) {
        T result = null;

        if (!Validator.isEmpty(key)) {
            CacheItem<T> item;
            synchronized (cache) {
                item = cache.get(key);

                if (item == null) {
                    item = new CacheItem<>();
                    item.element = element;
                    cache.put(key, item);
                } else if (isAlive(item)) {
                    result = item.element;
                    item.element = element;
                }
            }

            item.timestamp = System.currentTimeMillis();
        }

        return result;
    }

    /**
     * remove an element
     *
     * @param key key of element
     * @return removed element
     */
    public T remove(String key) {
        T result = null;

        if (!Validator.isEmpty(key)) {
            CacheItem<T> item;
            synchronized (cache) {
                item = cache.remove(key);
            }

            if (isAlive(item)) {
                result = item.element;
            }
        }

        return result;
    }

    /**
     * @param item item to check
     * @return false if item is expired or null
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean isAlive(CacheItem<T> item) {
        return isAlive(item, timeToLive);
    }

    /**
     * @return the timeToLive
     */
    public int getTimeToLive() {
        return (int) (timeToLive / 1000);
    }

    /**
     * Dispose all resources
     */
    public void dispose() {
        cache.clear();

        evictor.terminate();
    }

    /**
     * Evictor thread for {@link Cache}
     *
     * @author lempel@gmail.com
     * @since 2014. 3. 20.
     */
    private class CacheEvictor extends TerminatableThread {
        private final Map<String, CacheItem<T>> cache;
        private final long timeToLive;
        private final long interval;

        /**
         * @param cache cache to handle
         * @param timeToLive life span in seconds
         */
        public CacheEvictor(Map<String, CacheItem<T>> cache, long timeToLive) {
            this.cache = cache;
            this.timeToLive = timeToLive;
            interval = timeToLive / 3;
        }

        @Override
        public void run() {
            while (running && !terminated) {
                try {
                    sleep(interval);

                    Set<String> keys = cache.keySet();
                    for (String key : keys) {
                        CacheItem<T> item = cache.get(key);

                        if (!Cache.isAlive(item, timeToLive)) {
                            cache.remove(key);
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}