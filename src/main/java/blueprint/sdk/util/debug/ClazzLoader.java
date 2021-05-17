/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.debug;

/**
 * Loads Class from byte[]
 *
 * @author lempel@gmail.com
 * @since 2016-05-30
 */
public class ClazzLoader extends ClassLoader {
    public Class findClass(String name, byte classByte[]) {
        return defineClass(name, classByte, 0, classByte.length);
    }
}
