/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.config;

/**
 * Exception in {@link Config}
 *
 * @author lempel@gmail.com
 * @since 2013. 6. 18.
 */
public class ConfigException extends Exception {
    private static final long serialVersionUID = -5173477076800328832L;

    /**
     * Constructor
     *
     * @param message detailed message
     */
    public ConfigException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message detailed message
     * @param cause   cause
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     *
     * @param cause cause
     */
    public ConfigException(Throwable cause) {
        super(cause);
    }
}
