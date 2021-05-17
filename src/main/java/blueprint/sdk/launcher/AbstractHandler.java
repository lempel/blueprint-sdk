/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.launcher;

import blueprint.sdk.util.config.Config;

import javax.xml.xpath.XPathExpressionException;

/**
 * Prototype for handlers
 *
 * @author lempel@gmail.com
 * @since 2007. 12. 12
 */
public abstract class AbstractHandler {
    static final String pathSeparator;

    static {
        // check OS
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");

        if (isWindows) {
            pathSeparator = ";";
        } else {
            pathSeparator = ":";
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected final Config config;

    /**
     * @param config configuration
     */
    @SuppressWarnings("WeakerAccess")
    public AbstractHandler(final Config config) {
        this.config = config;
    }

    /**
     * Launches target program
     *
     * @param args additional arguments for target program
     * @throws XPathExpressionException
     */
    public abstract void launch(String[] args) throws XPathExpressionException;
}