/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.cache;

import java.util.HashMap;
import java.util.Map;

import blueprint.sdk.util.Validator;

/**
 * Very simple Object cache with lazy eviction.
 * 
 * @param <T>
 *            element type
 * @author Sangmin Lee
 * @since 2013. 6. 28.
 */
public class Cache<T> {
	/** actual cache */
	protected Map<String, CacheItem<T>> cache;

	/** time to live (in milli-seconds, 0 = no eviction) */
	protected long timeToLive;

	/**
	 * @param initialSize
	 *            initial size of cache
	 * @param timeToLive
	 *            time to live (in seconds, 0 = no eviction)
	 */
	public Cache(int initialSize, int timeToLive) {
		super();

		cache = new HashMap<String, CacheItem<T>>(initialSize);

		this.timeToLive = timeToLive * 1000;
	}

	/**
	 * get an element
	 * 
	 * @param key
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
	 * @param key
	 * @param element
	 * @return previous element
	 */
	public T put(String key, T element) {
		T result = null;

		if (!Validator.isEmpty(key)) {
			CacheItem<T> item = null;
			synchronized (cache) {
				item = cache.get(key);

				if (item == null) {
					item = new CacheItem<T>();
					item.element = element;
					cache.put(key, item);
				} else if (isAlive(item)) {
					result = item.element;
				}
			}

			item.timestamp = System.currentTimeMillis();
		}

		return result;
	}

	/**
	 * remove an element
	 * 
	 * @param key
	 * @return removed element
	 */
	public T remove(String key) {
		T result = null;

		if (!Validator.isEmpty(key)) {
			CacheItem<T> item = null;
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
	 * @param item
	 *            item to check
	 * @return false if item is expired or null
	 */
	protected boolean isAlive(CacheItem<T> item) {
		boolean result = false;

		if (item != null && item.timestamp >= (System.currentTimeMillis() - timeToLive)) {
			result = true;
		}

		return result;
	}

	/**
	 * @return the timeToLive
	 */
	public int getTimeToLive() {
		return (int) (timeToLive / 1000);
	}
}
