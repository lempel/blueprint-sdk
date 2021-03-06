/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
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
     * @return list of all available ip address except loopback
     * @throws SocketException I/O error occurs
     */
    public static List<InetAddress> getAllAddress() throws SocketException {
        return getAllAddress(true);
    }

    /**
     * @param excludeLoopback set to exclude loopback
     * @return list of all available ip address
     * @throws SocketException I/O error occurs
     */
    public static List<InetAddress> getAllAddress(boolean excludeLoopback) throws SocketException {
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
                if (excludeLoopback && addr.isLoopbackAddress()) {
                    continue;
                }
                result.add(addr);
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

    /**
     * Find a reachable address to target
     *
     * @param sources source addresses
     * @param target  target address
     * @return a source which can reach to target or null (not found)
     * @throws UnknownHostException target is unknown
     */
    public static InetAddress findReachable(List<String> sources, String target) throws UnknownHostException {
        return findReachable(sources.toArray(new String[0]), target);
    }

    /**
     * Find a reachable address to target
     *
     * @param sources source addresses
     * @param target  target address
     * @return a source which can reach to target or null (not found)
     * @throws UnknownHostException target is unknown
     */
    public static InetAddress findReachable(String[] sources, String target) throws UnknownHostException {
        return findReachable(sources, InetAddress.getByName(target));
    }

    /**
     * Find a reachable address to target
     *
     * @param sources source addresses
     * @param target  target address
     * @return a source which can reach to target or null (not found)
     * @throws UnknownHostException target is unknown
     */
    public static InetAddress findReachable(String[] sources, InetAddress target) throws UnknownHostException {
        InetAddress[] addresses = new InetAddress[sources.length];
        for (int i = 0; i < sources.length; i++) {
            if (!Validator.isEmpty(sources[i])) {
                addresses[i] = InetAddress.getByName(sources[i]);
            }
        }

        return findReachable(addresses, target);
    }

    /**
     * Find a reachable address to target
     *
     * @param sources source addresses
     * @param target  target address
     * @return a source which can reach to target or null (not found)
     * @throws UnknownHostException target is unknown
     */
    public static InetAddress findReachable(InetAddress[] sources, String target) throws UnknownHostException {
        return findReachable(sources, InetAddress.getByName(target));
    }

    /**
     * Find a reachable address to target
     *
     * @param sources source addresses
     * @param target  target address
     * @return a source which can reach to target or null (not found)
     */
    public static InetAddress findReachable(List<InetAddress> sources, InetAddress target) {
        return findReachable(sources.toArray(new InetAddress[0]), target);
    }

    /**
     * Find a reachable address to target
     *
     * @param sources source addresses
     * @param target  target address
     * @return a source which can reach to target or null (not found)
     */
    public static InetAddress findReachable(InetAddress[] sources, InetAddress target) {
        InetAddress result = null;

        if (target != null) {
            if (isLoopbackIp(target)) {
                // find loopback address
                for (InetAddress source : sources) {
                    if (isLoopbackIp(source)) {
                        result = source;
                        break;
                    }
                }
            } else if (isPrivateIp(target)) {
                byte[] targetAddr = target.getAddress();

                // find private address on same local network
                for (InetAddress source : sources) {
                    if (isPrivateIp(source)) {
                        byte[] sourceAddr = source.getAddress();

                        if (targetAddr.length != sourceAddr.length) {
                            continue;
                        }

                        if (targetAddr[0] == 10 && sourceAddr[0] == 10) {
                            result = source;
                            break;
                        } else if (targetAddr[0] == (byte) 172 && sourceAddr[0] == (byte) 172
                                && targetAddr[1] == sourceAddr[1]) {
                            result = source;
                            break;
                        } else if (targetAddr[0] == (byte) 192 && targetAddr[1] == (byte) 168 &&
                                targetAddr[0] == sourceAddr[0] && targetAddr[1] == sourceAddr[1]) {
                            result = source;
                            break;
                        }
                    }
                }
            } else {
                // find public address
                for (InetAddress source : sources) {
                    if (isPublicIp(source)) {
                        result = source;
                        break;
                    }
                }
            }
        }

        return result;
    }
}
