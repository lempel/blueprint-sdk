/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

/**
 * Simple Thread with implementation of {@link Terminatable}
 * 
 * @author Simon Lee
 * @since 2013. 8. 21.
 */
public class TerminatableThread extends Thread implements Terminatable {
	/** Reset to induce termination */
	protected boolean running = false;
	/** Sets when terminated */
	protected boolean terminated = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see blueprint.sdk.util.Terminatable#isValid()
	 */
	@Override
	public boolean isValid() {
		return running;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blueprint.sdk.util.Terminatable#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see blueprint.sdk.util.Terminatable#terminate()
	 */
	@Override
	public void terminate() {
		running = false;
	}
}
