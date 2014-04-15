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
 * Statistics Utility<br>
 * Simple yet...<br>
 * 
 * @since 2009. 2. 25.
 * @author Sangmin Lee
 */
public class Statistics {
	private long sum = 0;
	private long count = 0;

	public void add(final long amount) {
		synchronized (this) {
			sum += amount;
			count++;
		}
	}

	/**
	 * @return current mean
	 */
	public long mean() {
		long result;
		synchronized (this) {
			if (sum == 0) {
				result = 0;
			} else {
				result = sum / count;
			}
		}
		return result;
	}

	/**
	 * @return current sum
	 */
	public long sum() {
		synchronized (this) {
			return sum;
		}
	}

	/**
	 * @return current count
	 */
	public long count() {
		synchronized (this) {
			return count;
		}
	}

	public void reset() {
		synchronized (this) {
			sum = 0;
			count = 0;
		}
	}
}
