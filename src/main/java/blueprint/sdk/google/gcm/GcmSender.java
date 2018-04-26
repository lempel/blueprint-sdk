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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blueprint.sdk.google.gcm.bind.Request;
import blueprint.sdk.google.gcm.bind.Response;
import blueprint.sdk.util.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sends push message to GCM.
 *
 * @author lempel@gmail.com
 * @since 2013. 12. 3.
 */
public class GcmSender {
    private static final Logger L = LoggerFactory.getLogger(GcmSender.class);

    @SuppressWarnings("CanBeFinal")
    public static String GCM_URL = "https://android.googleapis.com/gcm/send";

    /**
     * jackson ObjectMapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    private final String apiKey;

    /**
     * Constructor
     *
     * @param apiKey API key for GCM
     */
    public GcmSender(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Send to GCM
     *
     * @param json    JSON message to send
     * @param retries Number of retry attempts. Can be 0.
     * @return response from GCM
     * @throws MalformedURLException Wrong GCM_URL value
     * @throws IOException           I/O error with GCM_URL
     */
    public GcmResponse send(String json, int retries) throws IOException {
        GcmResponse result = send(json);

        int interval = 1;
        for (int i = 0; i <= retries && result.needRetry(); i++) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignored) {
            }

            result = send(json);

            // simplified exponential back-off
            interval *= 2;
        }

        return result;
    }

    private GcmResponse send(String json) throws IOException {
        HttpURLConnection http = (HttpURLConnection) new URL(GCM_URL).openConnection();
        http.setRequestMethod("POST");
        http.addRequestProperty("Authorization", "key=" + apiKey);
        http.addRequestProperty("Content-Type", "application/json");

        http.setDoOutput(true);
        OutputStream os = http.getOutputStream();
        os.write(json.getBytes());
        os.close();

        http.connect();

        return decodeResponse(http);
    }

    /**
     * decodes response from GCM
     *
     * @param http HTTP connection
     * @return response from GCM
     * @throws IOException
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private GcmResponse decodeResponse(HttpURLConnection http) throws IOException {
        GcmResponse result = new GcmResponse();
        result.code = getResponseCode(http);

        if (result.code == HttpURLConnection.HTTP_OK) {
            try {
                Response response = mapper.readValue((InputStream) http.getContent(), Response.class);
                result.multicastId = response.multicast_id;
                result.success = response.success;
                result.failure = response.failure;
                result.canonicalIds = response.canonical_ids;

                // decode 'results'
                for (Map<String, String> item : response.results) {
                    GcmResponseDetail detail = new GcmResponseDetail();

                    if (item.containsKey("message_id")) {
                        detail.success = true;
                        detail.message = item.get("message_id");
                    } else {
                        detail.success = false;
                        detail.message = item.get("error");
                    }

                    result.results.add(detail);
                }
            } catch (Exception e) {
                result.code = GcmResponse.ERR_JSON_BIND;

                L.warn("Can't bind json", e);
            }
        } else if (result.code == GcmResponse.ERR_NOT_JSON) {
            int contentLength = http.getContentLength();
            String contentType = http.getContentType();

            InputStream ins = (InputStream) http.getContent();
            byte[] buffer = new byte[contentLength];
            ins.read(buffer);

            L.warn("response message is not a json. content-type=" + contentType + ", content=" + new String(buffer));
        }

        return result;
    }

    /**
     * gets response code
     *
     * @param http HTTP connection
     * @return 200: http ok, 6xx: {@link GcmResponse}, others: http error
     * @throws IOException
     */
    @SuppressWarnings("IndexOfReplaceableByContains")
    private int getResponseCode(HttpURLConnection http) throws IOException {
        int result = http.getResponseCode();

        if (result == HttpURLConnection.HTTP_OK) {
            int contentLength = http.getContentLength();
            String contentType = http.getContentType();
            if (0 == contentLength) {
                result = GcmResponse.ERR_NO_CONTENT;
            } else if (Validator.isEmpty(contentType)) {
                result = GcmResponse.ERR_NO_CONTENT_TYPE;
            } else if (0 > contentType.indexOf("application/json")) {
                result = GcmResponse.ERR_NOT_JSON;
            } else {
                result = 200;
            }
        }

        return result;
    }

    /**
     * Send to GCM
     *
     * @param regIds  list of client's registration id
     * @param data    data to send
     * @param retries Number of retry attempts. Can be 0.
     * @return response from GCM
     * @throws MalformedURLException Wrong GCM_URL value
     * @throws IOException           I/O error with GCM_URL
     */
    public GcmResponse send(String[] regIds, Map<String, String> data, int retries) throws
            IOException {
        Request request = new Request();
        request.registration_ids = regIds;
        request.data = data;

        return send(mapper.writeValueAsString(request), retries);
    }

    /**
     * Send to GCM
     *
     * @param regId   client's registration id
     * @param data    data to send
     * @param retries Number of retry attempts. Can be 0.
     * @return response from GCM
     * @throws MalformedURLException Wrong GCM_URL value
     * @throws IOException           I/O error with GCM_URL
     */
    public GcmResponse send(String regId, Map<String, String> data, int retries) throws
            IOException {
        return send(new String[]{regId}, data, retries);
    }
}