/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.google.gcm.spool;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blueprint.sdk.google.gcm.GcmResponse;
import blueprint.sdk.google.gcm.GcmResponseDetail;
import blueprint.sdk.google.gcm.GcmSender;
import blueprint.sdk.google.gcm.bind.Request;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles GCM error
 *
 * @author lempel@gmail.com
 * @since 2013. 12. 11.
 * @deprecated GCM is replaced by FCM - https://firebase.google.com/docs/cloud-messaging
 */
@SuppressWarnings("WeakerAccess")
public class GcmErrorHandler {
    private static final Logger L = LoggerFactory.getLogger(GcmErrorHandler.class);

    /**
     * Jackson ObjectMapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Handles errors in GCM's response
     *
     * @param request  request message to GCM (json)
     * @param response return value of {@link GcmSender}
     */
    public void handlerGcmError(String request, GcmResponse response) {
        if (response.failure > 0) {
            String[] regIds = getRegIds(request);

            for (int i = 0; i < response.results.size(); i++) {
                GcmResponseDetail detail = response.results.get(i);

                if (!detail.success) {
                    logGcmError(regIds[i], detail);
                }
            }
        }
    }

    void logGcmError(String regId, GcmResponseDetail detail) {
        L.info("GCM error on id '" + regId + "' - " + detail.message);
    }

    /**
     * Handles I/O error with GCM
     *
     * @param request request message to GCM (json)
     * @param exIo    related exception
     */
    public void handleIoError(String request, IOException exIo) {
        String[] regIds = getRegIds(request);

        for (String regId : regIds) {
            logIoError(exIo, regId);
        }
    }

    void logIoError(IOException exIo, String regId) {
        L.info("Can't send to id '" + regId + "' due to " + exIo.getMessage());
    }

    /**
     * @param request request message to GCM (json)
     * @return array of registration id
     */
    private String[] getRegIds(String request) {
        String[] result;

        try {
            Request binding = mapper.readValue(request, Request.class);
            result = binding.registration_ids;
        } catch (IOException e) {
            L.warn("Can't read json - " + request, e);
            result = new String[]{};
        }

        return result;
    }
}
