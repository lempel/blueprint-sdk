/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package blueprint.sdk.util.jvm;

import sun.jvmstat.monitor.*;
import sun.tools.jps.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Get information of running JVMs.<br>
 * <br>
 * Based on sun.tool.jsp.Jps.<br>
 *
 * @author lempel@gmail.com
 * @since 1.5
 */
public class JavaProcesses {
    private Arguments arguments = new Arguments(new String[]{"-mlv"});

    /**
     * @param monitoredHost host
     * @param info VM information
     * @return target VM
     */
    @SuppressWarnings("WeakerAccess")
    public static MonitoredVm getMonitoredVm(MonitoredHost monitoredHost, VmInfo info) {
        MonitoredVm result = null;

        String vmidString = toVmId(info.pid);

        try {
            VmIdentifier id = new VmIdentifier(vmidString);
            result = monitoredHost.getMonitoredVm(id, 0);
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * @param pid process id
     * @return VM id
     */
    private static String toVmId(int pid) {
        return "//" + pid + "?mode=r";
    }

    /**
     * @param arguments new arguments
     */
    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    /**
     * @return Summary of running JVMs
     * @throws MonitorException jvmstat Exception
     */
    public List<VmInfo> listJvms() throws MonitorException {
        List<VmInfo> result = new ArrayList<>();

        MonitoredHost monitoredHost = getMonitoredHost();

        // get the set active JVMs on the specified host.
        Set<Integer> jvms = monitoredHost.activeVms();

        for (Integer jvm : jvms) {
            VmInfo info = new VmInfo();

            info.pid = jvm;

            MonitoredVm vm = getMonitoredVm(monitoredHost, info);
            if (vm == null) {
                continue;
            }

            info.mainClass = MonitoredVmUtil.mainClass(vm, arguments.showLongPaths());
            info.mainArgs = MonitoredVmUtil.mainArgs(vm);
            info.vmArgs = MonitoredVmUtil.jvmArgs(vm);
            info.vmFlags = MonitoredVmUtil.jvmFlags(vm);

            monitoredHost.detach(vm);

            result.add(info);
        }

        return result;
    }

    /**
     * @return host of JVMs
     * @throws MonitorException
     */
    public MonitoredHost getMonitoredHost() throws MonitorException {
        return MonitoredHost.getMonitoredHost(arguments.hostId());
    }
}
