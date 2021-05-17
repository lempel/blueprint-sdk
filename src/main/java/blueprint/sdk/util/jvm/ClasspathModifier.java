/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.jvm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Adds jar/zip file to classpath during <b>run-time</b>.<br>
 * During compile-time, you still have to add library files.<br>
 * By calling addFile(String) or addURI(URI), classpath can be extended.<br>
 * <br>
 * example:<br>
 * try {<br>
 * addFile("a/b/c/d/e.jar");<br>
 * }<br>
 * catch (IOException e) {<br>
 * e.printStackTrace();<br>
 * }<br>
 *
 * @author lempel@gmail.com
 * @since 2009. 2. 4.
 */
public class ClasspathModifier {
    private static final Class<?>[] PARAMS = new Class[]{URL.class};

    public static void addPath(String path) throws IOException {
        File f = new File(path);
        addPath(f);
    }

    @SuppressWarnings("WeakerAccess")
    public static void addPath(File file) throws IOException {
        addURL(file.toURI().toURL());
    }

    @SuppressWarnings("WeakerAccess")
    public static void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMS);
            method.setAccessible(true);
            method.invoke(sysloader, u);
        } catch (SecurityException e) {
            throw new IOException("", e);
        } catch (IllegalArgumentException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Should not happen
            throw new IOException("Can't call URLClassLoader.addURL(URL)", e);
        }
    }
}
