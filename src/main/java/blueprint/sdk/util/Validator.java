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
     * Assert target String matches given date format (target can't be empty)
     *
     * @param target target String
     * @param name   name of target
     * @param format {@link FastDateFormat}
     */
    public static void assertDate(String target, String name, FastDateFormat format) {
        assertDate(target, name, format, false);
    }

    /**
     * Assert target String matches given date format
     *
     * @param target     target String
     * @param name       name of target
     * @param format     {@link FastDateFormat}
     * @param allowEmpty true: target can be empty
     */
    public static void assertDate(String target, String name, FastDateFormat format, boolean allowEmpty) {
        if (format == null) {
            throw new IllegalArgumentException("format can't be null");
        } else {
            if (isEmpty(target)) {
                if (!allowEmpty) {
                    throw new IllegalArgumentException(name + " should be " + format.getPattern());
                }
            } else {
                try {
                    format.parse(target);
                } catch (ParseException e) {
                    throw new IllegalArgumentException(name + " should be " + format.getPattern());
                }
            }
        }
    }

    /**
     * Assert target is not null
     *
     * @param target target Object
     * @param name   name of target
     */
    public static void assertNotNull(Object target, String name) {
        if (target == null) {
            throw new IllegalArgumentException(name + " can't be null");
        }
    }

    /**
     * Assert target is neither null nor empty
     *
     * @param target target Object
     * @param name   name of target
     */
    public static void assertNotEmpty(Object target, String name) {
        if (isEmpty(target)) {
            throw new IllegalArgumentException(name + " can't be empty");
        }
    }

    /**
     * Assert that at least one target is neither null nor empty
     *
     * @param targets target Objects
     * @param names   name of targets (must be in same order as targets)
     */
    public static void assertNotEmpty(Object[] targets, String[] names) {
        if (isEmpty(targets)) {
            throw new IllegalArgumentException("no targets");
        }
        if (isEmpty(names)) {
            throw new IllegalArgumentException("no names");
        }
        if (targets.length != names.length) {
            throw new IllegalArgumentException("length of targets and names are not equal");
        }

        boolean found = false;
        for (int i = 0; i < targets.length; i++) {
            if (isNotEmpty(targets[i])) {
                found = true;
                break;
            }
        }

        if (!found) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < names.length; i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(names[i]);
            }

            throw new IllegalArgumentException("at least one of (" + builder + ") shouldn't be empty");
        }
    }

    /**
     * return null if given value is null, 'null' or 'undefined'
     *
     * @param value String to test
     * @return value or null
     */
    public static String nullify(String value) {
        String ret = value;

        if (value == null || "null".equalsIgnoreCase(value) || "undefined".equalsIgnoreCase(value)) {
            ret = null;
        }

        return ret;
    }

    /**
     * see the Object is null or not
     *
     * @param value any Object
     * @return true: value is null
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isNull(Object value) {
        String valueStr = String.valueOf(value);
        return "null".equalsIgnoreCase(valueStr) || "undefined".equalsIgnoreCase(valueStr);
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
     * See if given value is empty or null
     *
     * @param value any Object
     * @return true: value is empty or null
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isEmpty(Object value) {
        boolean ret = isNull(value);

        if (!ret) {
            if (value instanceof String) {
                ret = ((String) value).trim().length() == 0;
            } else if (value instanceof Collection) {
                ret = ((Collection) value).isEmpty();
            } else if (value instanceof Map) {
                ret = ((Map) value).isEmpty();
            } else if (value instanceof Object[]) {
                ret = ((Object[]) value).length <= 0;
            }
        }

        return ret;
    }

    /**
     * See if given value is neither null nor empty
     *
     * @param value any Object
     * @return true: value is neither null nor empty
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isNotEmpty(Object value) {
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
