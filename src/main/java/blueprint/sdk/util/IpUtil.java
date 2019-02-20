/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * IP address related utility
 *
 * @author lempel@gmail.com
 * @since 2013. 8. 5.
 */
@SuppressWarnings("WeakerAccess")
public class IpUtil {
    /**
     * @return list of all available ip address
     * @throws SocketException I/O error occurs
     */
    public static List<InetAddress> getAllAddress() throws SocketException {
        List<InetAddress> result = new ArrayList<>();

        Enumeration<NetworkInterface> infs = NetworkInterface.getNetworkInterfaces();
        while (infs.hasMoreElements()) {
            NetworkInterface inf = infs.nextElement();
            if (!inf.isUp() || inf.isVirtual()) {
                continue;
            }

            Enumeration<InetAddress> addrs = inf.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress()) {
                    result.add(addr);
                }
            }
        }

        return result;
    }

    /**
     * @return list of all available ip address
     * @throws SocketException I/O error occurs
     */
    public static List<String> getAllIp() throws SocketException {
        List<String> result = new ArrayList<>();

        List<InetAddress> addrs = getAllAddress();
        for (InetAddress addr : addrs) {
            result.add(addr.getHostAddress());
        }

        return result;
    }

    /**
     * See if given address is private or not
     *
     * @param addr target address
     * @return true : private address
     */
    public static boolean isPrivateIp(InetAddress addr) {
        boolean result = false;

        if (addr instanceof Inet4Address) {
            result = isPrivateIp((Inet4Address) addr);
        } else if (addr instanceof Inet6Address) {
            result = isPrivateIp((Inet6Address) addr);
        }

        return result;
    }

    /**
     * See if given address is private or not
     *
     * @param addr IPv4 address
     * @return true : private address
     */
    public static boolean isPrivateIp(Inet4Address addr) {
        boolean result = false;

        byte[] address = addr.getAddress();

        if (address[0] == 10) {
            // 10.*.*.*
            result = true;
        } else if ((address[0] & 0x000000ff) == 0xa9 && (address[1] & 0x000000ff) == 0xfe) {
            // 169.254.*.*
            result = true;
        } else if ((address[0] & 0x000000ff) == 0xac
                && ((address[1] & 0x000000ff) >= 16 && (address[1] & 0x000000ff) <= 31)) {
            // 172.16.*.* ~ 172.31.*.*
            result = true;
        } else if ((address[0] & 0x000000ff) == 0xc0 && (address[1] & 0x000000ff) == 0xa8) {
            // 192.168.*.*
            result = true;
        }

        return result;
    }

    /**
     * See if given address is private or not
     *
     * @param addr IPv6 address
     * @return true : private address
     */
    public static boolean isPrivateIp(Inet6Address addr) {
        boolean result = false;

        byte[] address = addr.getAddress();

        if ((address[0] & 0x000000ff) == 0xfc && (address[1] & 0x000000ff) == 0x00) {
            result = true;
        } else if ((address[0] & 0x000000ff) == 0xfe && (address[1] & 0x000000ff) == 0x80) {
            result = true;
        }

        return result;
    }

    /**
     * See if given address is loopback or not
     *
     * @param addr target address
     * @return true : private address
     */
    public static boolean isLoopbackIp(InetAddress addr) {
        return addr.isLoopbackAddress();
    }

    /**
     * See if given address is public or not
     *
     * @param addr target address
     * @return true : public address
     */
    public static boolean isPublicIp(InetAddress addr) {
        return !(isPrivateIp(addr) || isLoopbackIp(addr));
    }
}
