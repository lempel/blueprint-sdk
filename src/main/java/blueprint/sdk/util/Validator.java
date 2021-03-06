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

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

/**
 * Provides Object validation methods
 *
 * @author lempel@gmail.com
 * @since 2009. 8. 22.
 */
public final class Validator {
    /**
     * see the Object is null or not
     *
     * @param value any Object
     * @return true: value is null
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isNull(Object value) {
        return value == null;
    }

    /**
     * see the Object is null or not
     *
     * @param value any Object
     * @return true: value is not null
     */
    public static boolean isNotNull(Object value) {
        return !isNull(value);
    }

    /**
     * see the String is null or empty
     *
     * @param value any String
     * @return true: value is null or empty
     */
    public static boolean isEmpty(String value) {
        return isNull(value) || (value.trim().length() == 0);
    }

    /**
     * see the array is null or empty
     *
     * @param value any array
     * @return true: value is null or empty
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isEmpty(Object[] value) {
        return isNull(value) || (value.length <= 0);
    }

    /**
     * see the Collection is null or empty
     *
     * @param value any Collection
     * @return true: value is null or empty
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isEmpty(Collection<?> value) {
        return isNull(value) || (value.isEmpty());
    }

    /**
     * see the Map is null or empty
     *
     * @param value any Map
     * @return true: value is null or empty
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isEmpty(Map<?, ?> value) {
        return isNull(value) || (value.isEmpty());
    }

    /**
     * see the String is null or empty
     *
     * @param value any String
     * @return true: value is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * see the array is null or empty
     *
     * @param value any array
     * @return true: value is not null and not empty
     */
    public static boolean isNotEmpty(Object[] value) {
        return !isEmpty(value);
    }

    /**
     * see the Collection is null or empty
     *
     * @param value any Collection
     * @return true: value is not null and not empty
     */
    public static boolean isNotEmpty(Collection<?> value) {
        return !isEmpty(value);
    }

    /**
     * see the Map is null or empty
     *
     * @param value any Map
     * @return true: value is not null and not empty
     */
    public static boolean isNotEmpty(Map<?, ?> value) {
        return !isEmpty(value);
    }

    /**
     * if the Object is null, throws NullPointerException with message
     *
     * @param value any Object
     * @param name  Object's name for error message
     * @throws NullPointerException value is null
     */
    public static void checkNull(Object value, String name) throws NullPointerException {
        if (isNull(value)) {
            throw new NullPointerException("'" + name + "' is null");
        }
    }

    /**
     * if the String is null or empty, throws NullPointerException with message
     *
     * @param value any String
     * @param name  String's name for error message
     * @throws NullPointerException value is null
     */
    @SuppressWarnings("WeakerAccess")
    public static void checkEmpty(String value, String name) throws NullPointerException {
        if (isEmpty(value)) {
            throw new NullPointerException("'" + name + "' is empty");
        }
    }

    /**
     * substitutes null String
     *
     * @param value any String
     * @param subst default value
     * @return validated String
     */
    public static String nvl(String value, String subst) {
        String result;

        if (isNull(value)) {
            result = subst;
        } else {
            result = value;
        }

        return result;
    }

    /**
     * substitutes null String with empty String
     *
     * @param value any String
     * @return validated String
     */
    public static String nvl(String value) {
        String result;

        if (isNull(value)) {
            result = "";
        } else {
            result = value;
        }

        return result;
    }

    /**
     * see the String is integer or not
     *
     * @param value any String
     * @return true: integer
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
    public static boolean isInteger(String value) {
        boolean result = false;

        try {
            if (Validator.isNotEmpty(value)) {
                Integer.parseInt(value);
                result = true;
            }
        } catch (NumberFormatException ignored) {
        }

        return result;
    }

    /**
     * see the String is integer or not
     *
     * @param value any String
     * @return true: not an integer
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isNotInteger(String value) {
        return !isInteger(value);
    }

    /**
     * if the String is integer or not, throws NumberFormatException with
     * message
     *
     * @param value any String
     * @param name  String's name for error message
     * @throws NullPointerException value is not an integer
     */
    public static void checkInteger(String value, String name) throws NumberFormatException {
        Validator.checkEmpty(value, name);
        if (isNotInteger(value)) {
            throw new NumberFormatException("'" + name + "' is not an integer");
        }
    }

    /**
     * see the String is boolean or not
     *
     * @param value any String
     * @return true: boolean
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
    public static boolean isBoolean(String value) {
        boolean result = false;

        if (Validator.isNotEmpty(value)) {
            Boolean.parseBoolean(value);
            result = true;
        }

        return result;
    }

    /**
     * see the String is boolean or not
     *
     * @param value any String
     * @return true: not an boolean
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isNotBoolean(String value) {
        return !isBoolean(value);
    }

    /**
     * if the String is boolean or not, throws RuntimeException with message
     *
     * @param value any String
     * @param name  String's name for error message
     * @throws RuntimeException value is not an boolean
     */
    public static void checkBoolean(String value, String name) throws RuntimeException {
        Validator.checkEmpty(value, name);
        if (isNotBoolean(value)) {
            throw new RuntimeException("'" + name + "' is not an boolean");
        }
    }

    /**
     * see the SocketChannel is valid(not null, opened, connected) or not
     *
     * @param channel any SocketChannel
     * @return true: valid
     */
    public static boolean isValid(SocketChannel channel) {
        boolean result = false;
        if (channel != null && channel.isOpen() && channel.isConnected()) {
            result = true;
        }
        return result;
    }

    /**
     * see the Selector is valid(not null, opened) or not
     *
     * @param sel any Selector
     * @return true: valid
     */
    public static boolean isValid(Selector sel) {
        boolean result = false;
        if (sel != null && sel.isOpen()) {
            result = true;
        }
        return result;
    }

    private static final FastDateFormat TS_FORMAT_1 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final FastDateFormat TS_FORMAT_2 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final FastDateFormat TS_FORMAT_3 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final FastDateFormat TS_FORMAT_4 = FastDateFormat.getInstance("yyyyMMddHHmm");
    private static final FastDateFormat TS_FORMAT_5 = FastDateFormat.getInstance("yyyyMMddHHmmss");

    /**
     * See if given value is a timestamp or not (among known formats)
     *
     * @param val formatted String
     * @return true: it's a timestamp
     */
    public static boolean isTimestamp(String val) {
        boolean ret = true;

        try {
            TS_FORMAT_1.parse(val);
        } catch (NumberFormatException | ParseException e) {
            try {
                TS_FORMAT_2.parse(val);
            } catch (NumberFormatException | ParseException ex) {
                try {
                    TS_FORMAT_3.parse(val);
                } catch (NumberFormatException | ParseException exc) {
                    try {
                        TS_FORMAT_4.parse(val);
                    } catch (NumberFormatException | ParseException e1) {
                        try {
                            TS_FORMAT_5.parse(val);
                        } catch (NumberFormatException | ParseException e2) {
                            ret = false;
                        }
                    }
                }
            }
        }

        return ret;
    }
}
