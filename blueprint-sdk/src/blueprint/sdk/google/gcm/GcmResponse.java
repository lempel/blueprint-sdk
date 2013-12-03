/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm;

/**
 * Response message from GCM
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 3.
 */
public class GcmResponse {
	/**
	 * 200: success<br>
	 * 400: invalid JSON<br>
	 * 401: Unauthorized<br>
	 * 5xx: Internal server error (must retry)<br>
	 * 6xx: GcmSender error<br>
	 */
	public int code = 0;

	public String message;

	/**
	 * @return true if code is between 500 and 599
	 */
	public boolean needRetry() {
		boolean result = false;
		if (500 <= code && code <= 599) {
			result = true;
		}
		return result;
	}
}
