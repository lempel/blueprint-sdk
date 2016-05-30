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
