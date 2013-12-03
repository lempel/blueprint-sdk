/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 International - http://code.google.com/p/blueprint-sdk
 South Korea - http://lempel.egloos.com


 Background:

 blueprint-sdk is a java software development kit to protect other open source
 softwares' licenses. It's intended to provide light weight APIs for blueprints.
 Well... at least trying to.

 There are so many great open source projects now. Back in year 2000, there
 were not much to use. Even JDBC drivers were rare back then. Naturally, I have
 to implement many things by myself. Especially dynamic class loading, networking,
 scripting, logging and database interactions. It was time consuming. Now I can
 take my picks from open source projects.

 But I still need my own APIs. Most of my clients just don't understand open
 source licenses. They always want to have their own versions of open source
 projects but don't want to publish derivative works. They shouldn't use open
 source projects in the first place. So I need to have my own open source project
 to be free from derivation terms and also as a mediator between other open
 source projects and my client's requirements.

 Primary purpose of blueprint-sdk is not to violate other open source project's
 license terms.


 To commiters:

 License terms of the other software used by your source code should not be
 violated by using your source code. That's why blueprint-sdk is made for.
 Without that, all your contributions are welcomed and appreciated.
 */
package blueprint.sdk.launcher;

import javax.xml.xpath.XPathExpressionException;

import blueprint.sdk.util.config.Config;

/**
 * Prototype for handlers
 * 
 * @author Sangmin Lee
 * @since 2007. 12. 12
 */
public abstract class AbstractHandler {
	/** is this running on M$ Windows? */
	protected static boolean isWindows = false;

	protected static String pathSeparator = ":";

	static {
		// check OS
		if (System.getProperty("os.name").startsWith("Windows")) {
			isWindows = true;
		}

		if (isWindows) {
			pathSeparator = ";";
		}
	}

	protected Config config;

	/**
	 * @param config
	 */
	public AbstractHandler(final Config config) {
		this.config = config;
	}

	/**
	 * Launches target program
	 * 
	 * @param args
	 *            additional arguments for target program
	 * @throws XPathExpressionException
	 */
	public abstract void launch(String[] args) throws XPathExpressionException;
}