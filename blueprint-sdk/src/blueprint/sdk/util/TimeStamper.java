/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.util.Calendar;


/**
 * Pre-formatted timestamps.
 * 
 * @author Sangmin Lee
 * @since 2002. 07. 30
 */
public class TimeStamper {
	protected static Calendar now = null;

	/**
	 * @return yyyymmdd
	 */
	public static String getDateStamp() {
		now = Calendar.getInstance();
		byte[] tempArray;
		byte[] stamp = new byte[8];
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.YEAR)), 4).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 4);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.MONTH) + 1), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 4, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.DAY_OF_MONTH)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 6, 2);

		return new String(stamp);
	}

	/**
	 * @param days
	 *            +, - days
	 * @return yyyymmdd
	 */
	public static String getDateStampAfter(final int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		byte[] tempArray;
		byte[] stamp = new byte[8];
		tempArray = StringUtil.lpadZero(Integer.toString(cal.get(Calendar.YEAR)), 4).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 4);
		tempArray = StringUtil.lpadZero(Integer.toString(cal.get(Calendar.MONTH) + 1), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 4, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 6, 2);

		return new String(stamp);
	}

	/**
	 * @return yyyymmdd
	 */
	public static String tomorrow() {
		return getDateStampAfter(1);
	}

	/**
	 * @return hhMMss
	 */
	public static String getTimeStamp() {
		return getTimeStamp6();
	}

	/**
	 * @return hh
	 */
	public static String getTimeStamp2() {
		now = Calendar.getInstance();
		byte[] tempArray;
		byte[] stamp = new byte[2];
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.HOUR_OF_DAY)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 2);

		return new String(stamp);
	}

	/**
	 * @return hhMM
	 */
	public static String getTimeStamp4() {
		now = Calendar.getInstance();
		byte[] tempArray;
		byte[] stamp = new byte[4];
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.HOUR_OF_DAY)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.MINUTE)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 2, 2);

		return new String(stamp);
	}

	/**
	 * @return hhMMss
	 */
	public static String getTimeStamp6() {
		now = Calendar.getInstance();
		byte[] tempArray;
		byte[] stamp = new byte[6];
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.HOUR_OF_DAY)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.MINUTE)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 2, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.SECOND)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 4, 2);

		return new String(stamp);
	}

	/**
	 * @return hh:MM:ss
	 */
	public static String getTimeStamp8() {
		now = Calendar.getInstance();
		byte[] tempArray;
		byte[] stamp = new byte[8];
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.HOUR_OF_DAY)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 2);
		stamp[2] = ':';
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.MINUTE)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 3, 2);
		stamp[5] = ':';
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.SECOND)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 6, 2);

		return new String(stamp);
	}

	/**
	 * @return hhMMssSSS
	 */
	public static String getTimeStamp9() {
		now = Calendar.getInstance();
		byte[] tempArray;
		byte[] stamp = new byte[9];
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.HOUR_OF_DAY)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 0, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.MINUTE)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 2, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.SECOND)), 2).getBytes();
		System.arraycopy(tempArray, 0, stamp, 4, 2);
		tempArray = StringUtil.lpadZero(Integer.toString(now.get(Calendar.MILLISECOND)), 3).getBytes();
		System.arraycopy(tempArray, 0, stamp, 6, 3);

		return new String(stamp);
	}
}