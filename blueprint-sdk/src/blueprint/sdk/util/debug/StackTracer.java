/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.debug;

import org.apache.log4j.Logger;

/**
 * Utility for stack trace
 * 
 * @author Sangmin Lee
 * @since 2013. 9. 5.
 */
public class StackTracer {
	private static final Logger L = Logger.getLogger(StackTracer.class);

	/**
	 * Prints current stack trace
	 */
	public static void printStackTrace() {
		try {
			throw new Exception("printStackTrace");
		} catch (Exception e) {
			L.info("printStackTrace", e);
		}
	}
}
