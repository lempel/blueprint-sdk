/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.bind;

import java.util.Map;

/**
 * Request message for GCM
 *
 * @author Sangmin Lee
 * @since 2013. 12. 10.
 */
public class Request {
    public String[] registration_ids = null;
    public Map<String, String> data = null;
}