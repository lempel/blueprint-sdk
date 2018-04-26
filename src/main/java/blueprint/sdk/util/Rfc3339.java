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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert Date/Time between {@link Date} and RFC3339
 *
 * @author lempel@gmail.com
 * @since 2015. 04. 17
 */
public class Rfc3339 {
    // 2014-07-21T16:35:27.000Z / 2014-07-21T16:35:27.000+00:00
    private static final SimpleDateFormat FORMAT_1 = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    // 2014-07-21T16:35:27Z / 2014-07-21T16:35:27+00:00
    private static final SimpleDateFormat FORMAT_2 = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssXXX");

    /**
     * Convert {@link Date} to RFC3339 (GMT)
     *
     * @param date Java Date
     * @return RFC3339 (GMT)
     */
    public static String toString(Date date) {
        return toString(date, true);
    }

    /**
     * Convert {@link Date} to RFC3339 (GMT)
     *
     * @param date            Java Date
     * @param includeMillisec include milli second
     * @return RFC3339 (GMT)
     */
    public static String toString(Date date, boolean includeMillisec) {
        if (includeMillisec) {
            return FORMAT_1.format(date);
        } else {
            return FORMAT_2.format(date);
        }
    }

    /**
     * Convert RFC3339 (GMT) to {@link Date}
     *
     * @param date RFC3339 (GMT)
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
