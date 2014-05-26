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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * loads class every time (always hot!)
 * 
 * @author Sangmin Lee
 * @since 2009. 3. 26.
 */
public class EveryTimeLoader extends ClassLoader {
	private static final Logger L = Logger.getLogger(EveryTimeLoader.class);

	@SuppressWarnings("unused")
	private ClassLoader parent;
	private String classDir;

	private EveryTimeLoader(ClassLoader parent, String classDir) {
		super();

		this.parent = parent;
		this.classDir = classDir;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
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

	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> result = null;

		try {
			FileInputStream fis = new FileInputStream(classDir + EveryTimeLoader.reaplace(name, '.', '/') + ".class");
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();

			result = defineClass(name, buffer, 0, buffer.length);

			L.debug("class loaded  - " + classDir + EveryTimeLoader.reaplace(name, '.', '/') + ".class");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassNotFoundException();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return result;
	}

	private static String reaplace(String str, char srcChar, char targetChar) {
		char[] temp = str.toCharArray();
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == srcChar) {
				temp[i] = targetChar;
			}
		}
		return new String(temp);
	}
}
