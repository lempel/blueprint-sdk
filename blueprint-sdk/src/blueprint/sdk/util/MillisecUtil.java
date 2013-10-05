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
 * Provides common methods for millisecond handling
 * 
 * @author Sangmin Lee
 * @since 2009. 8. 22.
 */
public final class MillisecUtil {
	/**
	 * converts second to millisecond
	 * 
	 * @param sec
	 * @return
	 */
	public static long sec2msec(int sec) {
		return sec * 1000;
	}

	/**
	 * converts minute to millisecond
	 * 
	 * @param min
	 * @return
	 */
	public static long min2msec(int min) {
		return sec2msec(min * 60);
	}

	/**
	 * converts hour to millisecond
	 * 
	 * @param hour
	 * @return
	 */
	public static long hour2msec(int hour) {
		return min2msec(hour * 60);
	}

	/**
	 * converts day to millisecond
	 * 
	 * @param day
	 * @return
	 */
	public static long day2msec(int day) {
		return hour2msec(day * 24);
	}
}
