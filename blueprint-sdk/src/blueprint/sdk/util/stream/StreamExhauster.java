/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.stream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Read everything from given InputStream.
 * 
 * @author Sangmin Lee
 * @since 2012. 11. 28.
 */
public class StreamExhauster extends Thread {
	/** stream to exhaust */
	protected DataInputStream ins;
	/** true: print, false: discard */
	protected DataOutputStream dos;

	/**
	 * Silently exhanust input
	 * 
	 * @param ins
	 *            InputStrema to exhaust
	 */
	public StreamExhauster(final InputStream input) {
		this(input, null);
	}

	/**
	 * @param ins
	 *            InputStrema to exhaust
	 * @param out
	 *            OutputStream to redirect
	 */
	public StreamExhauster(final InputStream input, final OutputStream out) {
		super();

		setDaemon(true);
		ins = new DataInputStream(input);
		if (out != null) {
			dos = new DataOutputStream(out);
		}
	}

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
