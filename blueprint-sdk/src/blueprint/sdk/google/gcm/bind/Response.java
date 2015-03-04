/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.bind;

import java.util.List;
import java.util.Map;

/**
 * Response message from GCM
 *
 * @author Sangmin Lee
 * @since 2013. 12. 10.
 */
public class Response {
    public String multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Map<String, String>> results;
}