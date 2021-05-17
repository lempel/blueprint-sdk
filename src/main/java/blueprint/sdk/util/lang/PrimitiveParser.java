/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.lang;

import blueprint.sdk.util.Validator;
import blueprint.sdk.util.reflect.Crowbar;

import java.lang.reflect.Field;

/**
 * Wrapper of primitive type parsers
 *
 * @author lempel@gmail.com
 * @since 2016. 12. 02
 */
public class PrimitiveParser {
    private static final int TRACE_DEPTH = 4;

    /**
     * @param o target
     * @return Boolean.parseBoolean(o)
     */
    public static boolean parseBoolean(final Object o) {
        String s = String.valueOf(o).trim();
        return Boolean.parseBoolean(s);
    }

    /**
     * @param o target
     * @return Integer.parseInt(o)
     */
    public static int parseInt(final Object o) {
        return parseInt(o, 10);
    }

    /**
     * @param o     target
     * @param radix radix
     * @return Integer.parseInt(o)
     */
    public static int parseInt(final Object o, final int radix) {
        String s = String.valueOf(o).trim();
        try {
            return Integer.parseInt(s, radix);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseInt");
        }
    }

    /**
     * @param o target
     * @return Long.parseLong(o)
     */
    public static long parseLong(final Object o) {
        return parseLong(o, 10);
    }

    /**
     * @param o     target
     * @param radix radix
     * @return Long.parseLong(o)
     */
    public static long parseLong(final Object o, final int radix) {
        String s = String.valueOf(o).trim();
        try {
            return Long.parseLong(s, radix);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseLong");
        }
    }

    /**
     * @param o target
     * @return Float.parseFloat(o)
     */
    public static float parseFloat(final Object o) {
        String s = String.valueOf(o).trim();
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseFloat");
        }
    }

    /**
     * @param o target
     * @return Double.parseDouble(o)
     */
    public static double parseDouble(final Object o) {
        String s = String.valueOf(o).trim();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseDouble");
        }
    }

    /**
     * Enhance message of given exception and truncate unnecessary StackTrace
     *
     * @param e      target (will be modified)
     * @param method parser method name
     * @return e (modified)
     */
    private static NumberFormatException enhanceMessage(final NumberFormatException e, final String method) {
        try {
            StackTraceElement[] traces = e.getStackTrace();

            if (traces.length > TRACE_DEPTH) {
                if (Validator.isNotEmpty(method) && !method.equals(traces[TRACE_DEPTH].getMethodName())) {
                    Field detailMessage = Crowbar.getField(Throwable.class, "detailMessage");
                    detailMessage.set(e, e.getMessage() + " - " + traces[TRACE_DEPTH]);
                }

                Field stackTrace = Crowbar.getField(Throwable.class, "stackTrace");
                StackTraceElement[] replacement = new StackTraceElement[traces.length - TRACE_DEPTH];
                System.arraycopy(traces, TRACE_DEPTH, replacement, 0, replacement.length);
                traces = replacement;
                stackTrace.set(e, traces);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        return e;
    }
}
