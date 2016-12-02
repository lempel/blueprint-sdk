/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
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
    public static Boolean parseBoolean(final Object o) {
        String s = String.valueOf(o).trim();
        return Boolean.parseBoolean(s);
    }

    /**
     * @param o target
     * @return Integer.parseInt(o)
     */
    public static Integer parseInt(final Object o) {
        String s = String.valueOf(o).trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseInt");
        }
    }

    /**
     * @param o target
     * @return Long.parseLong(o)
     */
    public static Long parseLong(final Object o) {
        String s = String.valueOf(o).trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseLong");
        }
    }

    /**
     * @param o target
     * @return Float.parseFloat(o)
     */
    public static Float parseFloat(final Object o) {
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
    public static Double parseDouble(final Object o) {
        String s = String.valueOf(o).trim();
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw enhanceMessage(e, "parseLong");
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
