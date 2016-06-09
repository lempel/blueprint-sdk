/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.io.UnsupportedEncodingException;

/**
 * String Handling Utility
 *
 * @author lempel@gmail.com
 * @since 2002. 07. 30
 */
public class StringUtil {
    /**
     * file separator
     */
    private static final String lineSeparator = System
            .getProperty("line.separator");

    /**
     * convert bye[] to String representation of hexa decimal values
     *
     * @param data byte[] to convert
     * @return String representation of hexa decimal values
     */
    public static String toHex(final byte[] data) {
        StringBuilder buffer = new StringBuilder(data.length * 2);

        for (byte aData : data) {
            buffer.append(StringUtil.lpadZero(Integer.toHexString(aData), 2));
        }

        return buffer.toString();
    }

    /**
     * convert String representation of hexa decimal values to byte[]
     *
     * @param arg String representation of hexa decimal values
     * @return original byte[]
     */
    public static byte[] fromHex(String arg) {
        if (Validator.isEmpty(arg)) {
            return null;
        }
        if (arg.length() % 2 == 1) {
            throw new IllegalArgumentException("Argument should not have odd length");
        }

        byte[] result = new byte[arg.length() / 2];
        for (int i = 0; i < arg.length(); i += 2) {
            result[i / 2] = (byte) (Integer.parseInt(arg.substring(i, i + 2), 16) & 0xff);
        }
        return result;
    }

    /**
     * convert bye[] to hexa decimal dump
     *
     * @param data byte[] to convert
     * @return hexa decimal expression
     */
    public static String toHexDump(final byte[] data) {
        // 72bytes per line
        // offset 8bytes, 'h: ', hexa value + space 3*16bytes, '; ', sanitized
        // value 16bytes

        byte[] msgBytes = new byte[data.length];
        System.arraycopy(data, 0, msgBytes, 0, data.length);

        StringBuilder buffer = new StringBuilder(
                ((msgBytes.length / 16) + 1) * 72);
        int offset = 0;
        int div = msgBytes.length / 16;
        int mod = msgBytes.length % 16;

        // 16bytes at a time
        for (int i = 0; i < div; i++) {
            buffer.append(StringUtil.lpadZero(Integer.toHexString(offset), 8));
            buffer.append("h: ");
            for (int j = 0; j < 16; j++) {
                buffer.append(StringUtil.lpadZero(
                        Integer.toHexString(msgBytes[offset + j]), 2));
                buffer.append(' ');
                if (msgBytes[offset + j] == 0x0d
                        || msgBytes[offset + j] == 0x0a) {
                    msgBytes[offset + j] = 0x20;
                }
            }
            buffer.append("; ");
            buffer.append(new String(msgBytes, offset, 16));
            buffer.append(lineSeparator);
            offset += 16;
        }

        // last line
        if (mod > 0) {
            buffer.append(StringUtil.lpadZero(Integer.toHexString(offset), 8));
            buffer.append("h: ");
            for (int j = 0; j < 16; j++) {
                if (j < mod) {
                    buffer.append(StringUtil.lpadZero(
                            Integer.toHexString(msgBytes[offset + j]), 2));
                    buffer.append(' ');
                    if (msgBytes[offset + j] == 0x0d
                            || msgBytes[offset + j] == 0x0a) {
                        msgBytes[offset + j] = 0x20;
                    }
                } else {
                    buffer.append("   ");
                }
            }
            buffer.append("; ");
            buffer.append(new String(msgBytes, offset, mod));
            buffer.append(lineSeparator);
        }

        return buffer.toString();
    }

    /**
     * Convert given String to console friendly.<br>
     * Some ASCII characters can mess-up console as you know.<br>
     *
     * @param target target String
     * @return console-friendly String
     */
    @SuppressWarnings("UnusedParameters")
    public static String sanitize(final String target) {
        byte[] srcBytes;
        try {
            srcBytes = target.getBytes(CharsetUtil.getDefaultEncoding());
        } catch (UnsupportedEncodingException e) {
            srcBytes = target.getBytes();
        }

        int condition = 0;
        while (condition < srcBytes.length) {
            if ((srcBytes[condition] & 0xff) == 0x00) {
                srcBytes[condition] = 0x20;
            } else if (srcBytes[condition] < 0x80) // not korean letters
            {
                // replace bad characters with 0x2e
                if (((srcBytes[condition] & 0xff) < 0x0a)
                        || ((srcBytes[condition] & 0xff) > 0x0d && (srcBytes[condition] & 0xff) < 0x20)
                        || ((srcBytes[condition] & 0xff) == 0x0c)) {
                    srcBytes[condition] = 0x2e;
                }
            } else {
                condition += 1; // skip korean letters
            }

            condition += 1;
        }

        String result;
        try {
            result = new String(srcBytes, CharsetUtil.getDefaultEncoding());
        } catch (UnsupportedEncodingException e) {
            result = new String(srcBytes);
        }
        return result;
    }

    @SuppressWarnings("WeakerAccess")
    public static String nvl(Object value) {
        String result;
        if (value == null) {
            result = "";
        } else {
            result = value.toString().trim();
        }
        return result;
    }

    public static String nvl(Object value, String defaultValue) {
        String result = null;
        if (value != null) {
            result = value.toString();
        }
        if (Validator.isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }

    public static String rpadSpace(final String src, final int length) {
        byte[] sourceArray = nvl(src).getBytes();
        byte[] targetArray = new byte[length];
        int count;

        if (sourceArray.length > length) {
            count = length;
        } else {
            count = sourceArray.length;
        }

        System.arraycopy(sourceArray, 0, targetArray, 0, count);
        for (int i = count; i < length; i++) {
            targetArray[i] = ' ';
        }

        return new String(targetArray);
    }

    public static String lpadZero(final String src, final int length) {
        byte[] sourceArray = nvl(src).getBytes();
        byte[] targetArray = new byte[length];
        int count;

        int sourceStart = sourceArray.length - length;
        if (sourceStart > 0) {
            count = length;
        } else {
            sourceStart = 0;
            count = sourceArray.length;
        }
        int targetStart = length - count;

        for (int i = 0; i < targetStart; i++) {
            targetArray[i] = '0';
        }
        System.arraycopy(sourceArray, sourceStart, targetArray, targetStart,
                count);

        return new String(targetArray);
    }

    public static byte[] rpadNull(final String src, final int length) {
        byte[] result;
        try {
            result = rpadNull(
                    nvl(src).trim().getBytes(CharsetUtil.getDefaultEncoding()),
                    length);
        } catch (UnsupportedEncodingException e) {
            result = rpadNull(nvl(src).trim().getBytes(), length);
        }
        return result;
    }

    @SuppressWarnings("WeakerAccess")
    public static byte[] rpadNull(final byte[] src, final int length) {
        byte[] result = new byte[length];

        if (src.length > result.length) {
            System.arraycopy(src, 0, result, 0, result.length);
        } else {
            System.arraycopy(src, 0, result, 0, src.length);

            for (int i = src.length; i < length; i++) {
                result[i] = 0x00;
            }
        }

        return result;
    }

    public static String lpadSpace(final String src, final int length) {
        byte[] sourceArray = nvl(src).getBytes();
        byte[] targetArray = new byte[length];
        int count;

        if (sourceArray.length > length) {
            count = length;
        } else {
            count = sourceArray.length;
        }

        System.arraycopy(sourceArray, 0, targetArray, length - count, count);

        for (int i = 0; i < length - count; i++) {
            targetArray[i] = ' ';
        }

        return new String(targetArray);

    }

    public static String rtrim(String value, int maxLength) {
        String result = nvl(value).trim();

        if (result.length() > maxLength) {
            result = result.substring(0, maxLength);
        }

        return result;
    }

    public static String concatString(final String... str) {
        int length = 0;
        byte[][] temp = new byte[str.length][];
        int pos = 0;
        while (pos < str.length) {
            try {
                temp[pos] = str[pos].getBytes(CharsetUtil.getDefaultEncoding());
            } catch (UnsupportedEncodingException e) {
                temp[pos] = str[pos].getBytes();
            }
            length += temp[pos].length;
            pos++;
        }

        byte[] buff = new byte[length];
        pos = 0;
        for (int i = 0; i < str.length; i++) {
            System.arraycopy(temp[i], 0, buff, pos, temp[i].length);
            pos += temp[i].length;
            temp[i] = null;
        }

        String result;
        try {
            result = new String(buff, CharsetUtil.getDefaultEncoding());
        } catch (UnsupportedEncodingException e) {
            result = new String(buff);
        }
        return result;
    }

    public static String capitalize(final String str) {
        String result = null;

        if (str != null) {
            if (str.length() >= 2) {
                result = str.substring(0, 1).toUpperCase()
                        + str.substring(1).toLowerCase();
            } else {
                result = str.toUpperCase();
            }
        }

        return result;
    }
}
