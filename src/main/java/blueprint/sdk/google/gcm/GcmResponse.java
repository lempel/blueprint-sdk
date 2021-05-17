/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.google.gcm;

import java.util.ArrayList;
import java.util.List;

/**
 * Response message of {@link GcmSender}
 *
 * @author lempel@gmail.com
 * @since 2013. 12. 3.
 * @deprecated GCM is replaced by FCM - https://firebase.google.com/docs/cloud-messaging
 */
public class GcmResponse {
    /**
     * no content in http response
     */
    public static final int ERR_NO_CONTENT = 601;
    /**
     * no content-type in http response
     */
    public static final int ERR_NO_CONTENT_TYPE = 602;
    /**
     * content type is not json
     */
    public static final int ERR_NOT_JSON = 603;
    /**
     * something is wrong with json or data binding
     */
    public static final int ERR_JSON_BIND = 604;

    /**
     * 200: success<br>
     * 400: invalid JSON<br>
     * 401: Unauthorized<br>
     * 5xx: Internal server error (must retry)<br>
     * 6xx: GcmSender error.<br>
     */
    public int code = 0;

    /**
     * 'multicast_id' from GCM's response
     */
    public String multicastId = "";

    /**
     * 'success' from GCM's response
     */
    public int success = 0;

    /**
     * 'failure' from GCM's response
     */
    public int failure = 0;

    /**
     * 'canonical_ids' from GCM's response
     */
    public int canonicalIds = 0;

    /**
     * 'results' from GCM's response
     */
    public final List<GcmResponseDetail> results = new ArrayList<>();

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