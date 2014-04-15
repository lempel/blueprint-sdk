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

import org.apache.log4j.Logger;

import blueprint.sdk.util.config.Config;

/**
 * A Java application launcher for jar hell.<br>
 * Provides easier way to configure classpath.<br>
 * <br>
 * fork option:<br>
 * If on, target will be launched as a child process.<br>
 * <br>
 * waitFor option (only effective when fork is on):<br>
 * If on, launcher will redirect target's standard output/error streams to
 * console and wait for target to destroy. You don't have to kill separately
 * when waitFor option is on. Just kill Launcher.<br>
 * <br>
 * trace option (only effective when fork is on):<br>
 * To turn on trace function, you need a trace.jar (<a
 * href="http://x-15.org/tracing.htm">http://x-15.org/tracing.htm</a>) file and<br>
 * create a 'trace.properties' file on working directory<br>
 * 
 * @author Sangmin Lee
 * @since 2007. 12. 12
 */
public class JavaLauncher {
	/** logger */
	private static final Logger LOGGER = Logger.getLogger(JavaLauncher.class);

	/**
	 * Entry Point
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length < 1) {
			LOGGER.error("Usage: java " + JavaLauncher.class.getName()
					+ " <config file name> [additional arguments for target program]");
		} else {
			try {
				JavaLauncher jln = new JavaLauncher();

				Config config = new Config();
				config.load(args[0]);

				String[] args2 = new String[args.length - 1];
				System.arraycopy(args, 1, args2, 0, args2.length);

				jln.launch(config, args2);
			} catch (Exception e) {
				LOGGER.error(e.toString());
			}
		}
	}

	/**
	 * launches target program accorind to given configuration
	 * 
	 * @param config
	 *            configuration
	 * @param args
	 *            extra arguments
	 * @throws XPathExpressionException
	 */
	public void launch(final Config config, final String[] args) throws XPathExpressionException {
		AbstractHandler handler = HandlerFactory.getInstance(config);

		handler.launch(args);
	}
}
