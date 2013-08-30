/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jvm.shutdown;

import java.util.List;
import java.util.Vector;

import blueprint.sdk.util.Terminatable;

/**
 * A Shutdown Hook terminates all registered Terminatables
 * 
 * @author Simon Lee
 * @since 2007. 07. 26
 */
public class Terminator extends Thread {
	/** Singleton */
	private static final Terminator TER;

	/** terminatable objects */
	private transient List<Terminatable> terminatables = new Vector<Terminatable>(50, 5);

	static {
		TER = new Terminator();
		Runtime.getRuntime().addShutdownHook(TER);
	}

	public static Terminator getInstance() {
		return TER;
	}

	public void register(final Terminatable target) {
		terminatables.add(target);
	}

	public void unregister(final Terminatable target) {
		terminatables.remove(target);
	}

	public void run() {
		while (!terminatables.isEmpty()) {
			Terminatable ter = (Terminatable) terminatables.remove(0);
			if (ter != null) {
				ter.terminate();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		terminatables.clear();
		terminatables = null;

		super.finalize();
	}
}