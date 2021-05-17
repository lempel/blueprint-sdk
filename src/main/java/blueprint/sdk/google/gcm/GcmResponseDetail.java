/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.google.gcm;

/**
 * Element of 'results' from GCM's response message
 *
 * @author lempel@gmail.com
 * @since 2013. 12. 10.
 * @deprecated GCM is replaced by FCM - https://firebase.google.com/docs/cloud-messaging
 */
public class GcmResponseDetail {
    public boolean success = false;

    /**
     * if success is true, message id.<br>
     * if success is false, error message.<br>
     */
    public String message = "";
}
