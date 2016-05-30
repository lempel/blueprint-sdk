/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 International - http://code.google.com/p/blueprint-sdk
 South Korea - http://lempel.egloos.com


 Background:

 blueprint-sdk is a java software development kit to protect other open source
 software licenses. It's intended to provide light weight APIs for blueprints.
 Well... at least trying to.

 There are so many great open source projects now. Back in year 2000, there
 were not much to use. Even JDBC drivers were rare back then. Naturally, I have
 to implement many things by myself. Especially dynamic class loading, networking,
 scripting, logging and database interactions. It was time consuming. Now I can
 take my picks from open source projects.

 But I still need my own APIs. Most of my clients just don't understand open
 source licenses. They always want to have their own versions of open source
 projects but don't want to publish derivative works. They shouldn't use open
 source projects in the first place. So I need to have my own open source project
 to be free from derivation terms and also as a mediator between other open
 source projects and my client's requirements.

 Primary purpose of blueprint-sdk is not to violate other open source project's
 license terms.


 To committers:

 License terms of the other software used by your source code should not be
 violated by using your source code. That's why blueprint-sdk is made for.
 Without that, all your contributions are welcomed and appreciated.
 */
package blueprint.sdk.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blueprint.sdk.util.config.Config;
import blueprint.sdk.util.jvm.ClasspathModifier;

/**
 * Add all dir/jar/zip files to current classpath and load target program within
 * same process
 *
 * @author lempel@gmail.com
 * @since 2007. 12. 12
 */
public class LoadHandler extends AbstractHandler {
    private static final Logger LOGGER = LoggerFactory
	    .getLogger(LoadHandler.class);

    /**
     * @param config
     *            configuration
     */
    public LoadHandler(final Config config) {
	super(config);
    }

    @Override
    public void launch(String[] args) throws XPathExpressionException {
	String env = config.getString("/javaLauncher/env").trim();
	String invoke = config.getString("/javaLauncher/invoke").trim();
	String[] classDirs = config.getStringArray("/javaLauncher/classDir");
	String[] jarFiles = config.getStringArray("/javaLauncher/jarFile");
	String[] jarDirs = config.getStringArray("/javaLauncher/jarDir");

	List<String> targs = new ArrayList<>(args.length);
	Collections.addAll(targs, args);

	loadChild(env, invoke, targs, classDirs, jarFiles, jarDirs);
    }

    void loadChild(final String env, final String invoke,
	    final List<String> args, final String[] classDirs,
	    final String[] jarFiles, final String[] jarDirs) {
	setEnv(env);

	// for classpath
	StringBuilder builder = new StringBuilder(10240);

	// add directories first
	for (String dir : classDirs) {
	    try {
		dir = dir.trim();
		builder.append(dir).append(pathSeparator);
		ClasspathModifier.addPath(dir);
		LOGGER.info("class dir - " + dir);
	    } catch (IOException e) {
		LOGGER.error("can't class dir - " + dir, e);
	    }
	}

	// add jar file
	for (String file : jarFiles) {
	    try {
		file = file.trim();
		builder.append(file).append(pathSeparator);
		ClasspathModifier.addPath(file);
		LOGGER.info("jar file - " + file);
	    } catch (IOException e) {
		LOGGER.error("can't add jar file - " + file, e);
	    }
	}

	// search sub directories and add jar file
	for (String dir : jarDirs) {
	    dir = dir.trim();
	    builder.append(addJarFiles(dir));
	}

	System.setProperty("java.class.path", builder.toString());

	StringTokenizer stk = new StringTokenizer(invoke, " ");
	int invokeTokens = stk.countTokens();
	if (invokeTokens < 1) {
	    LOGGER.error("value of invoke tag can't be null");
	} else {
	    String mainClassName = stk.nextToken();
	    LOGGER.info("main class = " + mainClassName);

	    List<String> newArgsList = new ArrayList<>(20);
	    while (stk.hasMoreTokens()) {
		newArgsList.add(stk.nextToken());
	    }
	    newArgsList.addAll(args);

	    String[] newArgs = new String[newArgsList.size()];
	    newArgsList.toArray(newArgs);

	    try {
		Class<?> mainClass = Class.forName(mainClassName);
		Method mainMethod = mainClass.getDeclaredMethod("main",
			String[].class);
		mainMethod.invoke(null, new Object[] { newArgs });
	    } catch (ClassNotFoundException e) {
		LOGGER.error("No such class to invoke - " + mainClassName, e);
	    } catch (SecurityException | NoSuchMethodException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException e) {
		// Should not happen
		LOGGER.error("Can't call " + mainClassName
			+ ".main(String[]) - " + e);
	    }
	}
    }

    /**
     * add jar/zip files
     *
     * @param path
     *            path to search
     * @return classpath of found libraries
     */
    String addJarFiles(final String path) {
	String result;
	File targetDir = new File(path);

	if (targetDir.isDirectory()) {
	    StringBuilder buffer = new StringBuilder(1024);

	    File[] targetFiles = targetDir.listFiles();
	    for (File target : targetFiles != null ? targetFiles : new File[0]) {
		if (target.isDirectory()) {
		    // skip '.' and '..'
		    if (!".".equals(target.getName())
			    && !"..".equals(target.getName())) {
			buffer.append(addJarFiles(target.getAbsolutePath()));
		    }
		} else if (target.getName().toLowerCase().endsWith("jar")
			|| target.getName().toLowerCase().endsWith("zip")) {

		    try {
			// add all jar/zip files
			buffer.append(target.getAbsolutePath()).append(
				pathSeparator);
			ClasspathModifier.addPath(target.getAbsolutePath());
			LOGGER.info("jar file - " + target.getAbsolutePath());
		    } catch (IOException e) {
			LOGGER.error("can't add jar file - "
				+ target.getAbsolutePath(), e);
		    }
		}
	    }

	    result = buffer.toString();
	} else {
	    // if it's not a directory, return empty string
	    result = "";
	}

	return result;
    }

    void setEnv(final String env) {
	StringTokenizer stk = new StringTokenizer(env);
	while (stk.hasMoreTokens()) {
	    String token = stk.nextToken();

	    if (token.startsWith("-D")) {
		String value = "";

		int sep = token.indexOf('=');
		String key;
		if (sep >= 0) {
		    value = token.substring(sep + 1);
		    key = token.substring(2, sep);
		} else {
		    key = token.substring(2);
		}

		System.setProperty(key, value);
	    }
	}
    }
}