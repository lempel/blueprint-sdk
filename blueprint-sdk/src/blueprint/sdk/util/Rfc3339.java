/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert Date/Time betweed {@link Date} and RFC3339 (GMT)
 * 
 * @author Sangmin Lee
 * @since 2015. 04. 17
 */
public class Rfc3339 {
    // 2014-07-21T16:35:27.000Z
    private static final SimpleDateFormat format = new SimpleDateFormat(
	    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /**
     * Convert {@link Date} to RFC3339 (GMT)
     * 
     * @param date
     *            Java Date
     * @return RFC3339 (GMT)
     */
    public static String toString(Date date) {
	synchronized (format) {
	    return format.format(date);
	}
    }

    /**
     * Convert RFC3339 (GMT) to {@link Date}
     * 
     * @param date
     *            RFC3339 (GMT)
     * @return Java Date
     * @throws ParseException
     */
    public static Date fromString(final String date) throws ParseException {
	synchronized (format) {
	    return format.parse(date.trim());
	}
    }
}
