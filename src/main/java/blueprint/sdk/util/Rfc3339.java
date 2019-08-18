/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Convert Date/Time between {@link Date} and RFC3339
 *
 * @author lempel@gmail.com
 * @since 2015. 04. 17
 */
public class Rfc3339 {
    // 2014-07-21T16:35:27.000Z / 2014-07-21T16:35:27.000+00:00
    private static final FastDateFormat FORMAT_1 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    // 2014-07-21T16:35:27Z / 2014-07-21T16:35:27+00:00
    private static final FastDateFormat FORMAT_2 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssXXX");

    private String value;

    public Rfc3339(String rfc3339) {
        value = rfc3339;
    }

    public Rfc3339(Calendar calendar) {
        this(toString(calendar));
    }

    public Rfc3339(Date date) {
        this(toString(date));
    }

    /**
     * @return RFC3339 String
     */
    public String string() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * @return Equivalent {@link Date}
     * @throws ParseException Invalid RFC3339 String was provided to constructor
     */
    public Date date() throws ParseException {
        return fromString(value);
    }

    /**
     * @return Equivalent {@link Calendar}
     * @throws ParseException Invalid RFC3339 String was provided to constructor
     */
    public Calendar calendar() throws ParseException {
        Calendar ret = GregorianCalendar.getInstance();
        ret.setTime(date());

        return ret;
    }

    /**
     * Convert {@link Calendar} to RFC3339
     *
     * @param cal Java Calendar
     * @return RFC3339
     */
    public static String toString(Calendar cal) {
        return toString(cal.getTime(), true);
    }

    /**
     * Convert {@link Date} to RFC3339
     *
     * @param date Java Date
     * @return RFC3339
     */
    public static String toString(Date date) {
        return toString(date, true);
    }

    /**
     * Convert {@link Date} to RFC3339
     *
     * @param date            Java Date
     * @param includeMillisec include milli second
     * @return RFC3339
     */
    public static String toString(Date date, boolean includeMillisec) {
        if (includeMillisec) {
            return FORMAT_1.format(date);
        } else {
            return FORMAT_2.format(date);
        }
    }

    /**
     * Convert RFC3339 to {@link Date}
     *
     * @param date RFC3339
     * @return Java Date
     * @throws ParseException
     */
    public static Date fromString(final String date) throws ParseException {
        try {
            return FORMAT_1.parse(date.trim());
        } catch (ParseException e) {
            return FORMAT_2.parse(date.trim());
        }
    }
}
