/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.debug;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Loads specified class from designated directory every time
 *
 * @author lempel@gmail.com
 * @since 2009. 3. 26.
 */
public class EveryTimeLoader extends ClazzLoader {
    private final String classDir;

    public EveryTimeLoader(String classDir) {
        super();

        this.classDir = classDir;
    }

    protected synchronized Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class result = findLoadedClass(name);
        if (result == null) {
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(
                        classDir + System.getProperty("file.separator") + name.replaceAll("\\.", "/") + ".class"));
                byte[] buffer = new byte[dis.available()];
                dis.readFully(buffer);
                dis.close();

                result = defineClass(name, buffer, 0, buffer.length);
            } catch (IOException e) {
                ClassLoader systemLoader = getSystemClassLoader();
                result = systemLoader.loadClass(name);
            }
        }

        if (resolve)
            resolveClass(result);

        return result;
    }
}
