/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jvm.shutdown;

import blueprint.sdk.util.Terminatable;

/**
 * Simple Thread with implementation of {@link Terminatable}.<br>
 * Automatically registered to {@link Terminator} at creation, unregistered by
 * {@link TerminatableThread#terminate()}.<br>
 * 
 * @author Sangmin Lee
 * @since 2013. 8. 21.
 */
public class TerminatableThread extends Thread implements Terminatable {
	/** Reset to induce termination */
	protected boolean running = false;
	/** Sets when terminated */
	protected boolean terminated = false;

	/**
	 * Constructor;
	 */
	public TerminatableThread() {
		Terminator.getInstance().register(this);
	}

	@Override
	public boolean isValid() {
		return running;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	@Override
	public void terminate() {
		running = false;

		Terminator.getInstance().unregister(this);
	}
}
