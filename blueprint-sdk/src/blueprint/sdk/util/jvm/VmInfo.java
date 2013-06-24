/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.jvm;

/**
 * Summay of sun.jvmstat.monitor.MonitoredVm
 * 
 * @author Simon Lee
 * @since 2013. 6. 23.
 */
public class VmInfo {
	public int pid;
	public String mainClass;
	public String mainArgs;
	public String vmArgs;
	public String vmFlags;
}
