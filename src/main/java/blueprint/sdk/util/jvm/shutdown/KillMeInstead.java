/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.jvm.shutdown;

import blueprint.sdk.util.jvm.JpsHelper;
import blueprint.sdk.util.jvm.VmInfo;
import blueprint.sdk.util.stream.StreamExhauster;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Marker Process for graceful shutdown.<br>
 * <br>
 * If you kills a process of {@link KillMeInstead}, parent process will begin
 * shutdown procedure.<Br>
 *
 * @author lempel@gmail.com
 * @since 2013. 6. 22.
 */
public class KillMeInstead {
    /**
     * Launch {@link KillMeInstead} process and wait for shutdown
     *
     * @param callback Will be invoked after shutdown of {@link KillMeInstead}
     *                 process
     * @throws IOException Can't launch
     */
    @SuppressWarnings("unused")
    public static void launch(final Runnable callback) throws IOException {
        launch(callback, false);
    }

    /**
     * Launch {@link KillMeInstead} process and wait for shutdown
     *
     * @param callback Will be invoked after shutdown of {@link KillMeInstead}
     *                 process
     * @param print    true: print {@link KillMeInstead}'s output
     * @throws IOException Can't launch
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static void launch(final Runnable callback, boolean print) throws IOException {
        String sep = System.getProperty("file.separator");
        List<String> cmds = new ArrayList<>(20);
        cmds.add(System.getProperty("java.home") + sep + "bin" + sep + "java");
        cmds.add("-classpath");
        cmds.add(System.getProperty("java.class.path"));
        cmds.add(KillMeInstead.class.getName());
        cmds.add(getPid());

        ProcessBuilder bld = new ProcessBuilder(cmds);
        Process killMe = bld.start();

        OutputStream out = null;
        if (print) {
            out = System.out;
        }

        final Thread ex1 = new StreamExhauster(killMe.getErrorStream(), out);
        final Thread ex2 = new StreamExhauster(killMe.getInputStream(), out);
        ex1.start();
        ex2.start();

        Thread loop = new Thread(() -> {
            while (true) {
                if (ex1.isAlive() && ex2.isAlive()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    // if kill me is dead then invoke callback
                    callback.run();
                    break;
                }
            }
        });
        loop.setDaemon(true);
        loop.start();
    }

    /**
     * @return current JVM's pid or null (can't get)
     */
    @SuppressWarnings("WeakerAccess")
    public static String getPid() {
        String result = null;

        RuntimeMXBean rm = ManagementFactory.getRuntimeMXBean();
        String runtimeName = rm.getName();

        int idx = runtimeName.indexOf('@');
        if (idx >= 0) {
            result = runtimeName.substring(0, idx);
        }

        return result;
    }

    /**
     * @param args ["parent's pid"]
     * @throws FileNotFoundException Can't create *.pid file or *.err file
     */
    public static void main(String[] args) throws FileNotFoundException {
        String pid = getPid();

        try {
            if (args.length >= 1) {
                monitor(args);
            }

            if (pid == null || pid.isEmpty()) {
                // warn
                OperatingSystemMXBean osm = ManagementFactory.getOperatingSystemMXBean();
                PrintWriter pwr = new PrintWriter("killme.err");
                pwr.println("Can't get PID from RuntimeMXBean. Please contact committers.");
                pwr.println("OS = " + osm.getName());
                pwr.println("Version = " + osm.getVersion());
                pwr.println("ManagementFactory.getRuntimeMXBean().getName() = "
                        + ManagementFactory.getRuntimeMXBean().getName());
                pwr.close();
            }
        } catch (Exception e) {
            PrintWriter pwr = new PrintWriter(new File("killme.err"));
            e.printStackTrace(pwr);
            pwr.close();
        }
    }

    private static void monitor(String[] args) throws IOException {
        final String ppid = args[0];

        JpsHelper jps = new JpsHelper();

        boolean hasParent = true;
        while (hasParent) {
            try {
                // 10sec interval
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }

            // search parent
            List<VmInfo> vms = jps.listJvms();
            hasParent = false;
            for (VmInfo vm : vms) {
                if (vm.pid.equals(ppid)) {
                    hasParent = true;
                    break;
                }
            }
        }
    }
}
