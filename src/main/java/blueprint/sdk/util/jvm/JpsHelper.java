/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util.jvm;

import blueprint.sdk.util.LoggerHelper;
import blueprint.sdk.util.Validator;
import blueprint.sdk.util.stream.StreamExhauster;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get information of running JVMs.<br>
 *
 * @author lempel@gmail.com
 * @since 1.5
 */
public class JpsHelper {
    private static final Logger L = LoggerHelper.get();

    /**
     * List all visible JVMs
     *
     * @return Summary of running JVMs
     * @throws IOException Can't execute 'jps' (invalid or no 'JAVA_HOME' system variable)
     */
    public List<VmInfo> listJvms() throws IOException {
        List<VmInfo> result = new ArrayList<>();

        // it's hard to distinguish program arguments with vm arguments if you use '-l' and '-v' flags at once.

        Map<String, VmInfo> vmInfoMap = new HashMap<>();

        // pid cmd args
        String[] lines = executeJps("-ml");
        // parse jps output into pid, cmd, args
        if (lines != null) {
            for (String line : lines) {
                if (!Validator.isEmpty(line)) {
                    Tokenizer tokenizer = new Tokenizer(line);
                    VmInfo vmInfo = new VmInfo();
                    vmInfo.pid = tokenizer.next();
                    vmInfo.command = tokenizer.next();
                    vmInfo.args = tokenizer.remain();

                    if (!Validator.isEmpty(vmInfo.pid)) {
                        vmInfoMap.put(vmInfo.pid, vmInfo);
                    }
                }
            }
        }

        // pid cmd vmArgs
        lines = executeJps("-lv");
        // parse jps output into pid, vmArgs
        if (lines != null) {
            for (String line : lines) {
                if (!Validator.isEmpty(line)) {
                    Tokenizer tokenizer = new Tokenizer(line);
                    String pid = tokenizer.next();
                    // skip command
                    tokenizer.next();

                    if (!Validator.isEmpty(pid) && vmInfoMap.containsKey(pid)) {
                        VmInfo vmInfo = vmInfoMap.get(pid);
                        vmInfo.vmArgs = tokenizer.remain();
                        result.add(vmInfo);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Execute 'jps' with given flags and return it's output
     *
     * @param flags 'jps' flags
     * @return 'jps' output (standard output only)
     * @throws IOException Can't execute 'jps' (invalid or no 'JAVA_HOME' system variable)
     */
    private String[] executeJps(String flags) throws IOException {
        String[] result = null;

        Process jpsArgs = Runtime.getRuntime().exec(new String[]{"jps", flags});

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new StreamExhauster(jpsArgs.getInputStream(), outputStream).start();

        if (jpsArgs.isAlive()) {
            try {
                jpsArgs.waitFor();
            } catch (InterruptedException e) {
                L.error("Can't wait for 'jps' to finish. Results might be inaccurate.", e);
            }
        }

        byte[] outputBytes = outputStream.toByteArray();
        if (outputBytes != null && outputBytes.length > 0) {
            String output = new String(outputBytes);
            result = output.split("[\\r\\n]");
        }

        return result;
    }

    private class Tokenizer {
        private char[] chars;
        private int pos;

        private Tokenizer(String commandLine) {
            chars = commandLine.toCharArray();
            pos = 0;
        }

        private String next() {
            String result = null;

            int start = pos;
            for (; pos < chars.length; pos++) {
                if (chars[pos] == ' ') {
                    result = new String(chars, start, pos - start);
                    pos++;
                    break;
                }
            }

            return result;
        }

        private String remain() {
            String result = null;

            if (pos < chars.length) {
                result = new String(chars, pos, chars.length - pos);
            }

            return result;
        }
    }

    /**
     * @param pid process id of desired VM
     * @return main class or executable jar's name or null (not found)
     * @throws IOException Can't execute 'jps' (invalid or no 'JAVA_HOME' system variable)
     */
    public String findMainClass(String pid) throws IOException {
        String result = null;
        List<VmInfo> vms = listJvms();

        for (VmInfo vm : vms) {
            if (String.valueOf(vm.pid).equals(pid)) {
                result = vm.command;
            }
        }
        return result;
    }
}
