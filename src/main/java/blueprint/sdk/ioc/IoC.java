package blueprint.sdk.ioc;

import blueprint.sdk.util.Validator;
import blueprint.sdk.util.debug.ClazzLoader;
import blueprint.sdk.util.debug.EveryTimeLoader;
import blueprint.sdk.util.jvm.JavaProcesses;
import blueprint.sdk.util.jvm.shutdown.KillMeInstead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvmstat.monitor.MonitorException;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * IoC Container
 *
 * @author lempel@gmail.com
 * @since 2016-05-30
 */
public class IoC {
    private static final Logger L = LoggerFactory.getLogger(IoC.class);

    static {
        // TODO remove debug code
        L.debug("env -------------------------------------------------");
        Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            L.debug("{} = {}", key, env.get(key));
        }

        // TODO remove debug code
        L.debug("prop ------------------------------------------------");
        Properties prop = System.getProperties();
        for (Object key : prop.keySet()) {
            L.debug("{} = {}", key, prop.get(key));
        }

        new IoC().init();
    }

    public void init() {
        L.info("Initializing IoC Container");

        boolean isExecuableJar = false;
        String mainClass = null;
        String rootPackage = null;
        try {
            // current JVM's PID
            String pid = KillMeInstead.getPid();
            mainClass = new JavaProcesses().findMainClass(pid);

            String lowerCases = mainClass.toLowerCase();
            if (lowerCases.endsWith(".jar") || lowerCases.endsWith(".zip")) {
                rootPackage = "";
                isExecuableJar = true;
            } else {
                int idx = mainClass.lastIndexOf(".");

                if (idx >= 0) {
                    rootPackage = mainClass.substring(0, idx);
                }
            }
        } catch (MonitorException e) {
            throw new NullPointerException("Can't find current JVM's PID");
        }

        L.info("mainClass = {}, rootPackage = {}", mainClass, rootPackage);

        if (Validator.isEmpty(mainClass)) {
            throw new NullPointerException("Can't find current JVM's main class");
        }
        if (Validator.isNull(rootPackage)) {
            throw new NullPointerException("Can't find current JVM's root package");
        }

        String classpath = System.getProperty("java.class.path");
        String[] tokens = classpath.split(System.getProperty("path.separator"));
        for (String token : tokens) {
            L.debug(token);

            // TODO remove debug code
            checkClasses();
            System.gc();

            // TODO remove debug code
            EveryTimeLoader testLoader = new EveryTimeLoader("D:\\git\\blueprint-sdk\\build\\classes\\main");
            try {
                testLoader.loadClass("blueprint.sdk.google.gcm.GcmResponse");
                testLoader.loadClass("blueprint.sdk.google.gcm.GcmResponse");
                testLoader.loadClass("blueprint.sdk.google.gcm.GcmResponse");
            } catch (Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }

            if (!Validator.isEmpty(token)) {
                File target = new File(token);

                // TODO find child classes according to rootPackage
                // ---- look up class dirs if mainClass is class
                // ---- look up jar files if mainClass is jar

                if (isExecuableJar && token.endsWith(mainClass)) {
                    try {
                        ZipFile jar = new ZipFile(target);
                        // TODO implement tailor made class loader
                        EveryTimeLoader loader = new EveryTimeLoader(token);

                        Enumeration<? extends ZipEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();

                            String entryName = entry.getName();
                            if (!Validator.isEmpty(entryName) && entryName.endsWith(".class")) {
                                String className = entryName.replaceAll("/", ".").substring(0, entryName.lastIndexOf('.'));

                                try {
                                    DataInputStream ins = new DataInputStream(jar.getInputStream(entry));
                                    byte[] buffer = new byte[(int) entry.getSize()];
                                    ins.readFully(buffer);
                                    ins.close();
                                    Class clazz = loader.findClass(className, buffer);

                                    // TODO inspect clazz and instantiate and put to some map

                                    L.debug("{} loaded", className);
                                } catch (ClassFormatError e) {
                                    L.error("Invalid class format - {}, {}", className, e.toString());
                                } catch (IllegalAccessError e) {
                                    L.error("IllegalAccessError - {}, {}", className, e.toString());
                                } catch (NoClassDefFoundError e) {
                                    L.error("No class def found - {}, {}", className, e.toString());
                                } catch (LinkageError e) {
                                    L.error("LinkageError - {}, {}", className, e.toString());
                                } catch (SecurityException ignored) {
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (!isExecuableJar && target.isDirectory()) {
                    // TODO implement tailor made class loader
                    ClazzLoader loader = new ClazzLoader();

                    // TODO traverse class directory

                    // TODO find class file

                    // TODO inspect clazz and instantiate and put to some map
                }
            }
        }

        L.info("IoC Container initialized");
    }

    // TODO remove debug code
    private static void checkClasses() {
        ClassLoadingMXBean clm = ManagementFactory.getClassLoadingMXBean();
        String stat = clm.getLoadedClassCount() + "+" + clm.getUnloadedClassCount()
                + "=" + clm.getTotalLoadedClassCount();
        L.info(">> classes: {}", stat);
    }

    // TODO remove debug code
    public static void main(String[] args) {
    }
}
