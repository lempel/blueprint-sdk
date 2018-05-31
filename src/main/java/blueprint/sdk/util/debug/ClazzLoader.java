/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
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
