/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm;

/**
 * Element of 'results' from GCM's response message
 *
 * @author lempel@gmail.com
 * @since 2013. 12. 10.
 */
public class GcmResponseDetail {
    public boolean success = false;

    /**
     * if success is true, message id.<br>
     * if success is false, error message.<br>
     */
    public String message = "";
}
