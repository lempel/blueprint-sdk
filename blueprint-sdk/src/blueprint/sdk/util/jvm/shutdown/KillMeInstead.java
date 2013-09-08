/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jvm.shutdown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import blueprint.sdk.util.jvm.JavaProcesses;
import blueprint.sdk.util.jvm.VmInfo;
import blueprint.sdk.util.stream.StreamExhauster;

/**
 * Marker Process for graceful shutdown.<br>
 * <br>
 * If you kills a process of {@link KillMeInstead}, parent process will begin
 * shutdown procedure.<Br>
 * 
 * @author Simon Lee
 * @since 2013. 6. 22.
 */
public class KillMeInstead {
	/**
	 * Launch {@link KillMeInstead} process and wait for shutdown
	 * 
	 * @param callback
	 *            Will be invoked after shutdown of {@link KillMeInstead}
	 *            process
	 * @throws IOException
	 *             Can't launch
	 */
	public static void launch(final Runnable callback) throws IOException {
		launch(callback, false);
	}

	/**
	 * Launch {@link KillMeInstead} process and wait for shutdown
	 * 
	 * @param callback
	 *            Will be invoked after shutdown of {@link KillMeInstead}
	 *            process
	 * @param print
	 *            true: print {@link KillMeInstead}'s output
	 * @throws IOException
	 *             Can't launch
	 */
	public static void launch(final Runnable callback, boolean print) throws IOException {
		String sep = System.getProperty("file.separator");
		List<String> cmds = new ArrayList<String>(20);
		cmds.add(System.getProperty("java.home") + sep + "bin" + sep + "java");
		cmds.add("-classpath");
		cmds.add(System.getProperty("java.class.path"));
		cmds.add(KillMeInstead.class.getName());
		cmds.add(getPid());

		ProcessBuilder bld = new ProcessBuilder(cmds);
		Process proc = bld.start();

		final Thread ex1 = new StreamExhauster(proc.getErrorStream(), print);
		final Thread ex2 = new StreamExhauster(proc.getInputStream(), print);
		ex1.start();
		ex2.start();

		Thread loop = new Thread() {
			public void run() {
				while (true) {
					if (ex1.isAlive() && ex2.isAlive()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					} else {
						// if kill me is dead then invoke callback
						callback.run();
						break;
					}
				}
			}
		};
		loop.setDaemon(true);
		loop.start();
	}

	/**
	 * @return current JVM's pid or null (can't get)
	 */
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
	 * @param args
	 *            ["parent's pid"]
	 * @throws FileNotFoundException
	 *             Can't create *.pid file or *.err file
	 * @throws MonitorException
	 *             Can't monitor JVMs
	 */
	public static void main(String[] args) throws FileNotFoundException, MonitorException {
		String pid = getPid();

		try {
			if (args.length >= 1) {
				monitor(args);
			}

			if (pid == null || pid.isEmpty()) {
				// warn
				OperatingSystemMXBean osm = ManagementFactory.getOperatingSystemMXBean();
				PrintWriter pwr = new PrintWriter("killme.err");
				pwr.println("Can't get PID from RuntimeMXBean. Please contact commiters.");
				pwr.println("OS = " + osm.getName());
				pwr.println("Verson = " + osm.getVersion());
				pwr.println("ManagementFactory.getRuntimeMXBean().getName() = "
						+ ManagementFactory.getRuntimeMXBean().getName());
				pwr.flush();
				pwr.close();
			}
		} catch (Exception e) {
			PrintWriter pwr = new PrintWriter(new File("killme.err"));
			e.printStackTrace(pwr);
			pwr.flush();
			pwr.close();
		}
	}

	private static void monitor(String[] args) throws MonitorException {
		final int ppid = Integer.parseInt(args[0]);
		final Object lock = new Object();

		JavaProcesses jps = new JavaProcesses();

		// find parent vm and install listener
		List<VmInfo> vms = jps.listJvms();
		for (VmInfo vm : vms) {
			if (ppid == vm.pid) {
				MonitoredHost host = jps.getMonitoredHost();
				host.addHostListener(new HostListener() {
					@Override
					public void vmStatusChanged(VmStatusChangeEvent event) {
						@SuppressWarnings("unchecked")
						Set<Integer> deadPids = event.getTerminated();

						for (int deadPid : deadPids) {
							if (deadPid == ppid) {
								synchronized (lock) {
									// die with parent
									lock.notify();
								}
								break;
							}
						}
					}

					@Override
					public void disconnected(HostEvent event) {
						synchronized (lock) {
							// die with parent
							lock.notify();
						}
					}
				});
			}
		}

		boolean hasParent = true;
		while (hasParent) {
			try {
				// 10sec interval
				Thread.sleep(10000);
			} catch (InterruptedException ignored) {
			}

			// search parent - parent could be terminated without any event
			vms = jps.listJvms();
			hasParent = false;
			for (VmInfo vm : vms) {
				if (ppid == vm.pid) {
					hasParent = true;
					break;
				}
			}
		}
	}
}
