package blueprint.sdk.util;

/**
 * Utility for type checking.<br>
 *
 * @author lempel@gmail.com
 * @since 2021. 4. 20.
 */
public class TypeChecker {
    /**
     * @param value Object to test
     * @return true: value is an Integer
     */
    public static boolean isInteger(Object value) {
        boolean ret = false;

        if (!Validator.isNull(value)) {
            try {
                Integer.parseInt(value.toString());
                ret = true;
            } catch (NumberFormatException e) {
                ret = false;
            }
        }

        return ret;
    }
}
