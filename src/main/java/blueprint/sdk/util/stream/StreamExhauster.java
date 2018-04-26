/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util.stream;

import java.io.*;

/**
 * Read everything from given InputStream.
 *
 * @author lempel@gmail.com
 * @since 2012. 11. 28.
 */
public class StreamExhauster extends Thread {
    /**
     * stream to exhaust
     */
    private final DataInputStream ins;
    /**
     * true: print, false: discard
     */
    private DataOutputStream dos;

    /**
     * Silently exhanust input
     *
     * @param input InputStream to exhaust
     */
    public StreamExhauster(final InputStream input) {
        this(input, null);
    }

    /**
     * @param input  InputStream to exhaust
     * @param output OutputStream to redirect
     */
    public StreamExhauster(final InputStream input, final OutputStream output) {
        super();

        setDaemon(true);
        ins = new DataInputStream(input);
        if (output != null) {
            dos = new DataOutputStream(output);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try {
            while (true) {
                byte[] one = new byte[1];
                ins.readFully(one);
                if (dos != null) {
                    dos.write(one);
                }

                byte[] bin = new byte[ins.available()];
                ins.readFully(bin);
                if (dos != null) {
                    dos.write(bin);
                }
            }
        } catch (IOException ignored) { // NOPMD
        }
    }
}
