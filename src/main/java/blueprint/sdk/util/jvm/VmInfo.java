/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.jvm;

/**
 * Summary of sun.jvmstat.monitor.MonitoredVm
 *
 * @author lempel@gmail.com
 * @since 2013. 6. 23.
 */
public class VmInfo {
    public String pid = null;
    public String command = null;
    public String args = null;
    public String vmArgs = null;
}
