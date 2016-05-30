/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.debug;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * loads class every time (always hot!)
 *
 * @author lempel@gmail.com
 * @since 2009. 3. 26.
 */
public class EveryTimeLoader extends ClassLoader {
    private static final Logger L = LoggerFactory.getLogger(EveryTimeLoader.class);

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private ClassLoader parent;
    private final String classDir;

    private EveryTimeLoader(ClassLoader parent, String classDir) {
        super();

        this.parent = parent;
        this.classDir = classDir;
    }

    public static synchronized EveryTimeLoader newInstance(String classDir) {
        Thread currentThread = Thread.currentThread();
        ClassLoader ctxClassLoader = currentThread.getContextClassLoader();

        EveryTimeLoader newClassLoader;
        if (!(ctxClassLoader instanceof EveryTimeLoader)) {
            newClassLoader = new EveryTimeLoader(ctxClassLoader, classDir);
            currentThread.setContextClassLoader(newClassLoader);
        } else {
            newClassLoader = (EveryTimeLoader) ctxClassLoader;
        }

        return newClassLoader;
    }

    private static String replace(String str) {
        char[] temp = str.toCharArray();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == '.') {
                temp[i] = '/';
            }
        }
        return new String(temp);
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> result = null;

        try {
            FileInputStream fis = new FileInputStream(classDir + EveryTimeLoader.replace(name) + ".class");
            byte[] buffer = new byte[fis.available()];
            int nRead = fis.read(buffer);
            fis.close();

            result = defineClass(name, buffer, 0, nRead);

            L.debug("class loaded  - " + classDir + EveryTimeLoader.replace(name) + ".class");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }
}
