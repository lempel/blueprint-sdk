/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.launcher;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author lempel@gmail.com
 * @since 2007. 12. 12
 */
public class JavaLauncher {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaLauncher.class);

    /**
     * Entry Point
     *
     * @param args arguments
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
     * launches target program according to given configuration
     *
     * @param config configuration
     * @param args   extra arguments
     * @throws XPathExpressionException
     */
    @SuppressWarnings("WeakerAccess")
    public void launch(final Config config, final String[] args) throws XPathExpressionException {
        AbstractHandler handler = HandlerFactory.getInstance(config);

        handler.launch(args);
    }
}
