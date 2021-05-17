/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.lang;

/**
 * Unsigned Integer for interoperability with external systems.
 *
 * @author lempel@gmail.com
 * @since 2015-12-16
 */
public class UnsignedInteger {
    public static final long INT_MSB = 0x80000000L;
    public static final long SINT_MAX = 0x7FFFFFFFL;
    public static final long UINT_MAX = 0x0FFFFFFFL;

    /**
     * Carry flag.<br>
     * If it's set, value can't be expressed as signed int.<br>
     */
    protected boolean carry = false;
    /**
     * actual value w/o carry
     */
    protected int signedInt = 0;

    /**
     * Internal use only
     *
     * @param carry     carry flag
     * @param signedInt signed int value
     */
    protected UnsignedInteger(boolean carry, int signedInt) {
        this.carry = carry;
        this.signedInt = signedInt;
    }

    /**
     * @param value unsigned int
     */
    public UnsignedInteger(long value) {
        this((value & INT_MSB) != 0, (int) (value & SINT_MAX));
    }

    /**
     * @param value String representation of unsigned int
     */
    public UnsignedInteger(String value) {
        this(Long.parseLong(value));
    }

    /**
     * @param value          byte[] representation of unsigned int
     * @param isLittleEndian true for little endian, false for big endian
     */
    public UnsignedInteger(byte[] value, boolean isLittleEndian) {
        if (value == null) {
            throw new NullPointerException("Given value is null");
        }

        int start = 0;
        int end = value.length;
        int inc = 1;
        if (!isLittleEndian) {
            start = end - 1;
            end = -1;
            inc = -1;
        }

        long result = 0L;
        int shift = 0;
        for (int i = start; i != end; i += inc, shift += 8) {
            result += ((long) (value[i]) & 0xff) << shift;
            //System.out.println("result += " + i + " : " + ((long) (value[i]) & 0xff) + " << " + shift + "  == " + Long.toHexString(result));
        }

        carry = (result & INT_MSB) != 0;
        signedInt = (int) (result & SINT_MAX);
    }

    /**
     * @return an Exception for invalid scale
     */
    protected static NumberFormatException newNumberFormatException() {
        return new NumberFormatException("Can't be expressed as signed int");
    }

    /**
     * Get signed int value
     *
     * @param value String representation of unsigned int
     * @return signed int
     * @throws NumberFormatException Value can't be expressed as signed int
     */
    public static int intValue(String value) throws NumberFormatException {
        return new UnsignedInteger(value).intValue();
    }

    /**
     * Get signed int value
     *
     * @param value          byte[] representation of unsigned int
     * @param isLittleEndian true for little endian, false for big endian
     * @return signed int
     * @throws NumberFormatException Value can't be expressed as signed int
     */
    public static int intValue(byte[] value, boolean isLittleEndian) throws NumberFormatException {
        return new UnsignedInteger(value, isLittleEndian).intValue();
    }

    /**
     * Get unsigned int value as long type
     *
     * @param value String representation of unsigned int
     * @return unsigned int
     */
    public static long longValue(String value) {
        return new UnsignedInteger(value).longValue();
    }

    /**
     * Get unsigned int value as long type
     *
     * @param value          byte[] representation of unsigned int
     * @param isLittleEndian true for little endian, false for big endian.
     * @return unsigned int
     */
    public static long longValue(byte[] value, boolean isLittleEndian) {
        return new UnsignedInteger(value, isLittleEndian).longValue();
    }

    /**
     * See if wrapped value has carry or not
     *
     * @return true: has carry. can't be expressed as signed int.
     */
    public boolean hasCarry() {
        return carry;
    }

    /**
     * Get wrapped signed int value
     *
     * @return signed int
     */
    public int getSignedInt() {
        return signedInt;
    }

    /**
     * Get value as signed int
     *
     * @return signed int
     * @throws NumberFormatException Value can't be expressed as signed int
     */
    public int intValue() throws NumberFormatException {
        if (carry) {
            throw newNumberFormatException();
        } else {
            return signedInt;
        }
    }

    /**
     * Get unsinged int value as long type
     *
     * @return unsinged int value as long type
     */
    public long longValue() {
        return ((long) signedInt & SINT_MAX) | (hasCarry() ? INT_MSB : 0L);
    }

    /**
     * Get String representation of value
     *
     * @return String representation of value
     */
    public String toString() {
        return Long.toString(longValue());
    }

    /**
     * Get Hexadecimal String representation of value
     *
     * @return Hexadecimal String representation of value
     */
    public String toHexString() {
        return Long.toHexString(longValue());
    }

    /**
     * Get byte[] representation of value
     *
     * @param isLittleEndian true for little endian, false for big endian
     * @return byte[] representation of value
     */
    public byte[] toByteArray(boolean isLittleEndian) {
        byte[] result = new byte[4];

        int start = 0;
        int end = result.length;
        int inc = 1;
        if (!isLittleEndian) {
            start = end - 1;
            end = -1;
            inc = -1;
        }

        long longValue = longValue();
        int shift = 0;
        for (int i = start; i != end; i += inc, shift += 8) {
            result[i] = (byte) ((longValue >> shift) & 0xFF);
            //System.out.println("result[" + i + "] = " + Byte.toUnsignedInt(result[i]));
        }

        return result;
    }
}
