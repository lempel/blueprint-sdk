/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package bluerpint.sdk.util.stream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Read everything from given InputStream.
 * 
 * @author Simon Lee
 * @since 2012. 11. 28.
 */
public class StreamExhauster extends Thread {
	/** stream to exhaust */
	protected DataInputStream ins;
	/** true: print, false: discard */
	protected boolean print = false;

	/**
	 * @param ins
	 */
	public StreamExhauster(final InputStream ins) {
		this(ins, false);
	}

	/**
	 * @param ins
	 * @param print
	 *            true: print, false: discard
	 */
	public StreamExhauster(final InputStream ins, final boolean print) {
		super();
		this.ins = new DataInputStream(ins);
	}

	public void run() {
		try {
			while (true) {
				byte[] one = new byte[1];
				ins.readFully(one);
				if (print) {
					System.out.println(new String(one)); // NOPMD
				}

				byte[] bin = new byte[ins.available()];
				ins.readFully(bin);
				if (print) {
					System.out.println(new String(bin)); // NOPMD
				}
			}
		} catch (IOException ignored) { // NOPMD
		}
	}
}
