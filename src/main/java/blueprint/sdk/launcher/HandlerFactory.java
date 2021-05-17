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
 * Handler Factory for JavaLauncher
 *
 * @author lempel@gmail.com
 * @since 2007. 12. 12
 */
@SuppressWarnings("WeakerAccess")
public final class HandlerFactory {
    private HandlerFactory() {
        super();
    }

    /**
     * returns an instance of handler for given configuration
     *
     * @param config configuration
     * @return instance of handler
     * @throws XPathExpressionException
     */
    public static AbstractHandler getInstance(final Config config) throws XPathExpressionException {
        AbstractHandler result;

        boolean fork = config.getBoolean("/javaLauncher/invoke/@fork");

        if (fork) {
            result = new ForkHandler(config);
        } else {
            result = new LoadHandler(config);
        }

        return result;
    }

}