/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util;

/**
 * Provides common method for graceful shutdown
 *
 * @author lempel@gmail.com
 * @since 2007. 07. 18
 */
public interface Terminatable {
    boolean isValid();

    boolean isTerminated();

    void terminate();
}
