package blueprint.sdk.util.config;

/**
 * Config 내부에서 발생하는 Exception
 * 
 * @author Sangmin Lee
 * @since 2013. 6. 18.
 */
public class ConfigException extends Exception {
	private static final long serialVersionUID = -5173477076800328832L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            상세 메시지
	 */
	public ConfigException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            상세 메시지
	 * @param cause
	 *            발생 원인
	 */
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            발생 원인
	 */
	public ConfigException(Throwable cause) {
		super(cause);
	}
}
