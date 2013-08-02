/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;

/**
 * Provides Object validation methods
 * 
 * @author Simon Lee
 * @since 2009. 8. 22.
 */
public final class Validator {
	/**
	 * see the Object is null or not
	 * 
	 * @param value
	 * @return true: value is null
	 */
	public static boolean isNull(Object value) {
		return value == null ? true : false;
	}

	/**
	 * see the Object is null or not
	 * 
	 * @param value
	 * @return true: value is not null
	 */
	public static boolean isNotNull(Object value) {
		return !isNull(value);
	}

	/**
	 * see the String is null or empty
	 * 
	 * @param value
	 * @return true: value is null or empty
	 */
	public static boolean isEmpty(String value) {
		return isNull(value) ? true : (value.trim().length() == 0 ? true : false);
	}

	/**
	 * see the Collection is null or empty
	 * 
	 * @param value
	 * @return true: value is null or empty
	 */
	public static boolean isEmpty(Collection<?> value) {
		return isNull(value) ? true : (value.isEmpty() ? true : false);
	}

	/**
	 * see the String is null or empty
	 * 
	 * @param value
	 * @return true: value is not null and not empty
	 */
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	/**
	 * see the Collection is null or empty
	 * 
	 * @param value
	 * @return true: value is not null and not empty
	 */
	public static boolean isNotEmpty(Collection<?> value) {
		return !isEmpty(value);
	}

	/**
	 * if the Object is null, throws NullPointerException with message
	 * 
	 * @param value
	 * @param name
	 *            Object's name for message
	 * @throws NullPointerException
	 *             value is null
	 */
	public static void checkNull(Object value, String name) throws NullPointerException {
		if (isNull(value)) {
			throw new NullPointerException("'" + name + "' is null");
		}
	}

	/**
	 * if the String is null or empty, throws NullPointerException with message
	 * 
	 * @param value
	 * @param name
	 *            String's name for message
	 * @throws NullPointerException
	 *             value is null
	 */
	public static void checkEmpty(String value, String name) throws NullPointerException {
		if (isEmpty(value)) {
			throw new NullPointerException("'" + name + "' is empty");
		}
	}

	/**
	 * substitutes null String
	 * 
	 * @param value
	 * @return
	 */
	public static String nvl(String value, String subst) {
		String result;

		if (isNull(value)) {
			result = subst;
		} else {
			result = value;
		}

		return result;
	}

	/**
	 * substitutes null String with empty String
	 * 
	 * @param value
	 * @return
	 */
	public static String nvl(String value) {
		String result;

		if (isNull(value)) {
			result = "";
		} else {
			result = value;
		}

		return result;
	}

	/**
	 * see the String is integer or not
	 * 
	 * @param value
	 * @return true: integer
	 */
	public static boolean isInteger(String value) {
		boolean result = false;

		try {
			if (Validator.isNotEmpty(value)) {
				Integer.parseInt(value);
				result = true;
			}
		} catch (NumberFormatException e) {
		}

		return result;
	}

	/**
	 * see the String is integer or not
	 * 
	 * @param value
	 * @return true: not an integer
	 */
	public static boolean isNotInteger(String value) {
		return !isInteger(value);
	}

	/**
	 * if the String is integer or not, throws NumberFormatException with
	 * message
	 * 
	 * @param value
	 * @param name
	 *            String's name for message
	 * @throws NullPointerException
	 *             value is not an integer
	 */
	public static void checkInteger(String value, String name) throws NumberFormatException {
		Validator.checkEmpty(value, name);
		if (isNotInteger(value)) {
			throw new NumberFormatException("'" + name + "' is not an integer");
		}
	}

	/**
	 * see the String is boolean or not
	 * 
	 * @param value
	 * @return true: boolean
	 */
	public static boolean isBoolean(String value) {
		boolean result = false;

		if (Validator.isNotEmpty(value)) {
			Boolean.parseBoolean(value);
			result = true;
		}

		return result;
	}

	/**
	 * see the String is boolean or not
	 * 
	 * @param value
	 * @return true: not an boolean
	 */
	public static boolean isNotBoolean(String value) {
		return !isBoolean(value);
	}

	/**
	 * if the String is boolean or not, throws RuntimeException with message
	 * 
	 * @param value
	 * @param name
	 *            String's name for message
	 * @throws RuntimeException
	 *             value is not an boolean
	 */
	public static void checkBoolean(String value, String name) throws RuntimeException {
		Validator.checkEmpty(value, name);
		if (isNotBoolean(value)) {
			throw new RuntimeException("'" + name + "' is not an boolean");
		}
	}

	/**
	 * see the SocketChannel is valid(not null, opened, connected) or not
	 * 
	 * @param channel
	 * @return true: valid
	 */
	public static boolean isValid(SocketChannel channel) {
		boolean result = false;
		if (channel != null && channel.isOpen() && channel.isConnected()) {
			result = true;
		}
		return result;
	}

	/**
	 * see the Selector is valid(not null, opened) or not
	 * 
	 * @param sel
	 * @return true: valid
	 */
	public static boolean isValid(Selector sel) {
		boolean result = false;
		if (sel != null && sel.isOpen()) {
			result = true;
		}
		return result;
	}

	/**
	 * See if given address is private or not
	 * 
	 * @param addr
	 * @return true : private network
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
	 * @param addr
	 *            IPv4 address
	 * @return true : private network
	 */
	public static boolean isPrivateIp(Inet4Address addr) {
		boolean result = false;

		byte[] address = addr.getAddress();

		if (address[0] == 10) {
			result = true;
		} else if (address[0] == 172 && (address[1] >= 16 && address[1] <= 31)) {
			result = true;
		} else if (address[0] == 192 && address[1] == 168) {
			result = true;
		}

		return result;
	}

	/**
	 * See if given address is private or not
	 * 
	 * @param addr
	 *            IPv6 address
	 * @return true : private network
	 */
	public static boolean isPrivateIp(Inet6Address addr) {
		boolean result = false;

		byte[] address = addr.getAddress();

		// XXX not so sure about this
		if (address[0] == 0xfc && address[0] == 0x00) {
			result = true;
		}

		return result;
	}

	/**
	 * See if given address is loopback or not
	 * 
	 * @param addr
	 * @return true : private network
	 */
	public static boolean isLoopbackIp(InetAddress addr) {
		boolean result = false;

		if (addr instanceof Inet4Address) {
			result = isLoopbackIp((Inet4Address) addr);
		} else if (addr instanceof Inet6Address) {
			result = isLoopbackIp((Inet6Address) addr);
		}

		return result;
	}

	/**
	 * See if given address is loopback or not
	 * 
	 * @param addr
	 *            IPv4 address
	 * @return true : private network
	 */
	public static boolean isLoopbackIp(Inet4Address addr) {
		boolean result = false;

		byte[] address = addr.getAddress();

		if (address[0] == 127) {
			result = true;
		}

		return result;
	}

	/**
	 * See if given address is loopback or not
	 * 
	 * @param addr
	 *            IPv6 address
	 * @return true : private network
	 */
	public static boolean isLoopbackIp(Inet6Address addr) {
		boolean result = false;

		byte[] address = addr.getAddress();

		if (address[address.length - 1] == 1) {
			result = true;
		}

		return result;
	}
}
