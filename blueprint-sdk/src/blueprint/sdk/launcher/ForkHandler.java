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

import blueprint.sdk.util.CharsetUtil;
import blueprint.sdk.util.StringUtil;
import blueprint.sdk.util.Validator;
import blueprint.sdk.util.config.Config;
import org.apache.log4j.Logger;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Forks target program as a child process.
 *
 * @author Sangmin Lee
 * @since 2007. 12. 12
 */
public class ForkHandler extends AbstractHandler {
    private static final Logger LOGGER = Logger.getLogger(ForkHandler.class);

    private static final String traceJarName = "trace.jar";
    private static String traceJarLocation = null;

    public ForkHandler(final Config config) {
        super(config);
    }

    @Override
    public void launch(String[] args) throws XPathExpressionException {
        String env = config.getString("/javaLauncher/env").trim();
        String invoke = config.getString("/javaLauncher/invoke").trim();
        String[] classDirs = config.getStringArray("/javaLauncher/classDir");
        String[] jarFiles = config.getStringArray("/javaLauncher/jarFile");
        String[] jarDirs = config.getStringArray("/javaLauncher/jarDir");

        StringBuilder targsbld = new StringBuilder();
        for (String arg : args) {
            targsbld.append(" ").append(arg);
        }
        String targstr = targsbld.toString();

        boolean waitFor = config.getBoolean("/javaLauncher/invoke/@waitFor");
        boolean trace = config.getBoolean("/javaLauncher/invoke/@trace");

        new ForkHandler(config).forkChild(env, invoke, targstr, waitFor, trace, classDirs, jarFiles, jarDirs);
    }

    @SuppressWarnings("WeakerAccess")
    protected void forkChild(final String env, final String invoke, final String argstr, final boolean waitFor,
                             final boolean trace, final String[] classDirs, final String[] jarFiles, final String[] jarDirs) {
        // for classpath
        StringBuilder builder = new StringBuilder(10240);

        // add directories first
        for (String dir : classDirs) {
            dir = dir.trim();
            builder.append(dir.trim()).append(pathSeparator);
            LOGGER.info("class dir - " + dir);
        }

        // add jar file
        for (String file : jarFiles) {
            file = file.trim();
            builder.append(file).append(pathSeparator);
            LOGGER.info("jar file - " + file);

            // check 'trace.jar'
            if (file.endsWith(traceJarName)) {
                traceJarLocation = file;
            }
        }

        // search sub directories and add jar file
        for (String dir : jarDirs) {
            dir = dir.trim();
            builder.append(searchJarFiles(dir));
        }

        try {
            Runtime rtime = Runtime.getRuntime();

            // fork target(child) process
            Process proc;
            if (trace && Validator.isNotEmpty(traceJarLocation)) {
                proc = rtime.exec(StringUtil.concatString("java ", env, " -Xbootclasspath/a:", traceJarLocation,
                        " -javaagent:", traceJarLocation, " -cp ", builder.toString(), " ", invoke, argstr));
            } else {
                proc = rtime.exec(StringUtil.concatString("java ", env, " -cp ", builder.toString(), " ", invoke));
            }

            // Shutdown Hook for synchronized termination
            new ShutdownHook(proc);

            if (waitFor) {
                // stream tracker thread for target process
                class StreamTracker extends Thread {
                    private InputStream input = null;

                    private boolean runFlag = true;

                    public StreamTracker(final InputStream input) {
                        super();
                        this.input = input;
                    }

                    public void terminate() {
                        runFlag = false;
                    }

                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    public void run() {
                        while (runFlag) {
                            try {
                                byte[] one = new byte[1];
                                input.read(one);

                                byte[] buffer = new byte[input.available()];
                                if (buffer.length > 0) {
                                    input.read(buffer);
                                    // TODO need special appender for this
                                    // ---- or extract log level from message.
                                    LOGGER.info(new String(one) + new String(buffer, CharsetUtil.getDefaultEncoding()));
                                }
                            } catch (IOException e) {
                                LOGGER.error(e.toString());
                            }
                        }
                    }
                }

                // redirect target process's standard output/error
                StreamTracker trk1 = new StreamTracker(proc.getInputStream());
                StreamTracker trk2 = new StreamTracker(proc.getErrorStream());
                trk1.start();
                trk2.start();

                try {
                    proc.waitFor();
                } catch (InterruptedException e) {
                    LOGGER.error(e.toString());
                }

                trk1.terminate();
                trk2.terminate();
            }
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    /**
     * find jar/zip files
     *
     * @param path target dir to search
     * @return classpath of found libraries
     */
    @SuppressWarnings("WeakerAccess")
    protected String searchJarFiles(final String path) {
        String result;
        File targetDir = new File(path);

        if (targetDir.isDirectory()) {
            StringBuilder builder = new StringBuilder(1024);

            File[] targetFiles = targetDir.listFiles();
            for (File target : targetFiles != null ? targetFiles : new File[0]) {
                if (target == null) {
                    continue;
                }

                if (target.isDirectory()) {
                    // skip '.' and '..'
                    if (!".".equals(target.getName()) && !"..".equals(target.getName())) {
                        builder.append(searchJarFiles(target.getAbsolutePath()));
                    }
                } else if (target.getName().toLowerCase().endsWith("jar")
                        || target.getName().toLowerCase().endsWith("zip")) {

                    // add all jar/zip files
                    builder.append(target.getAbsolutePath()).append(pathSeparator);
                    LOGGER.info("jar file - " + target.getAbsolutePath());

                    // check trace.jar
                    if (target.getName().equals(traceJarName)) {
                        traceJarLocation = target.getAbsolutePath();
                    }
                }
            }

            result = builder.toString();
        } else {
            // if it's not a directory, return empty string
            result = "";
        }

        return result;
    }
}

/**
 * Shutdown Hook to destroy child process
 *
 * @author Sangmin Lee
 * @since 2007. 12. 12
 */
class ShutdownHook extends Thread {
    private final transient Process child;

    /**
     * Constructor
     *
     * @param child child process to monitor
     */
    public ShutdownHook(final Process child) {
        super();
        Runtime.getRuntime().addShutdownHook(this);
        this.child = child;
    }

    /*
     * (non-Javadoc, override method)
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        child.destroy();
    }
}