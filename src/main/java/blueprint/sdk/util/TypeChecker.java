/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

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
