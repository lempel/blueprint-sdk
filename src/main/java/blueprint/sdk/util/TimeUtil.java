/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Time handling Utility
 *
 * @author lempel@gmail.com
 * @since 2019-01-22
 */
public class TimeUtil {
    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    public static Date now() {
        return getCalendar().getTime();
    }

    public static Date now(TimeZone timeZone) {
        return getCalendar(timeZone).getTime();
    }

    public static Calendar getCalendar() {
        return getCalendar(TimeZone.getDefault());
    }

    public static Calendar getCalendar(TimeZone timeZone) {
        Calendar result = GregorianCalendar.getInstance();
        result.setTimeZone(timeZone);
        return result;
    }

    public static Calendar toCalendar(Date target) {
        Calendar ret = GregorianCalendar.getInstance();
        ret.setTime(target);
        return ret;
    }

    public static String timeToString(long time) {
        Calendar cal = getCalendar();
        cal.setTimeInMillis(time);
        return DATE_FORMAT.format(cal.getTime());
    }

    public static Calendar getToday() {
        Calendar cal = getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal;
    }

    public static Calendar getYesterday() {
        Calendar cal = getCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getDaysAfter(int days) {
        Calendar cal = getCalendar();
        cal.add(Calendar.DAY_OF_MONTH, days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static Calendar getMonthsAfter(int months) {
        Calendar cal = getCalendar();
        cal.add(Calendar.MONTH, months);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static int getLastDayOfMonth(Calendar cal) {
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns difference between two arguments in millisecond.
     *
     * @param cal1 argument 1
     * @param cal2 argument 2
     * @return millisecond
     */
    public static long diff(Calendar cal1, Calendar cal2) {
        return cal1.getTimeInMillis() - cal2.getTimeInMillis();
    }

    /**
     * Returns difference between two arguments in millisecond.
     *
     * @param cal1  argument 1
     * @param date2 argument 2
     * @return millisecond
     */
    public static long diff(Calendar cal1, Date date2) {
        return cal1.getTimeInMillis() - date2.getTime();
    }

    /**
     * Returns difference between two arguments in millisecond.
     *
     * @param date1 argument 1
     * @param cal2  argument 2
     * @return millisecond
     */
    public static long diff(Date date1, Calendar cal2) {
        return date1.getTime() - cal2.getTimeInMillis();
    }

    /**
     * Returns difference between two arguments in millisecond.
     *
     * @param date1 argument 1
     * @param date2 argument 2
     * @return millisecond
     */
    public static long diff(Date date1, Date date2) {
        return date1.getTime() - date2.getTime();
    }
}
