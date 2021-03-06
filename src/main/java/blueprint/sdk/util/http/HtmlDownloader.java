/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Download HTML or whatever the link leads as a String
 *
 * @author lempel@gmail.com
 * @since 2015. 03. 12
 */
public class HtmlDownloader {
    private static final Logger L = LoggerFactory.getLogger(HtmlDownloader.class);

    /**
     * Value for 'User-Agent' of HTTP header
     */
    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36";

    /**
     * Connect to given url
     *
     * @param target target url
     * @return connection
     * @throws IOException
     */
    private HttpURLConnection connect(URL target) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) target.openConnection();
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        connection.setRequestProperty("Accept-Language", "ko,en-US;q=0.8,en;q=0.6");

        connection.connect();

        return connection;
    }

    /**
     * Visit designated url and return response code.
     *
     * @param target target url
     * @return http response code
     * @throws IOException URI connection error
     */
    public int getResponseCode(String target) throws IOException {
        URL url = new URL(target);

        HttpURLConnection connection = connect(url);

        return connection.getResponseCode();
    }

    /**
     * Download designated url as a String
     *
     * @param target target url
     * @return contents of target
     * @throws IOException URL connection error
     */
    public String download(String target) throws IOException {
        String result;

        URL url = new URL(target);
        HttpURLConnection connection = connect(url);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            DataInputStream contentStream = new DataInputStream(new BufferedInputStream(
                    (InputStream) connection.getContent()));

            // default charset encoding
            String enc = "UTF-8";
            if (connection.getURL().getHost().endsWith("co.kr")) {
                enc = "EUC-KR";
            }

            // detect charset encoding
            String contentType = connection.getHeaderField("Content-Type");
            if (contentType != null) {
                StringTokenizer stk = new StringTokenizer(contentType,
                        " ,;:='\"");
                boolean hasCharset = false;
                while (stk.hasMoreElements()) {
                    String token = stk.nextToken();
                    //L.debug("token: " + token + " - " + url);
                    if (hasCharset) {
                        if (!token.isEmpty()) {
                            enc = token.toUpperCase();
                            break;
                        }
                    } else if ("CHARSET".equalsIgnoreCase(token)) {
                        hasCharset = true;
                    }
                }

                //L.debug("charset: " + enc + " - " + url);
            }

            int contentLength = connection.getContentLength();
            if (contentLength > 0) {
                byte[] buffer = new byte[contentLength];
                contentStream.readFully(buffer);

                result = new String(buffer, enc);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read = 0;
                boolean first = true;
                while ((read = contentStream.read(buffer, 0, buffer.length)) != -1) {
                    baos.write(buffer, 0, read);

                    // check first 4k for charset
                    if (first) {
                        String head = new String(buffer, "ISO-8859-1");
                        StringTokenizer stk = new StringTokenizer(head,
                                " ,;:='\"");
                        boolean hasCharset = false;
                        while (stk.hasMoreElements()) {
                            String token = stk.nextToken();
                            //L.debug("token: " + token + " - " + url);
                            if (hasCharset) {
                                if (!token.isEmpty()) {
                                    enc = token.toUpperCase();
                                    break;
                                }
                            } else if ("CHARSET".equalsIgnoreCase(token)) {
                                hasCharset = true;
                            }
                        }
                        //L.debug("charset: " + enc + " - " + url);

                        first = false;
                    }
                }

                result = new String(baos.toByteArray(), enc);
            }
        } else {
            result = connection.getResponseMessage();
        }

        L.debug("download - " + result.length() + " bytes - " + url);

        return result;
    }
}
