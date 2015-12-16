/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.lang;

/**
 * Signed Integer for interoperability with external systems.
 *
 * @author Sangmi Lee
 * @since 2015-12-16
 */
public class SignedInteger {
    /**
     * actual value w/o carry
     */
    protected int signedInt = 0;

    /**
     * @param value unsigned int
     */
    public SignedInteger(int value) {
        signedInt = value;
    }

    /**
     * @param value String representation of unsigned int
     */
    public SignedInteger(String value) {
        this(Integer.parseInt(value));
    }

    /**
     * @param value          byte[] representation of unsigned int
     * @param isLittleEndian true for little endian, false for big endian
     * @throws NumberFormatException Value can't be expressed as signed int
     */
    public SignedInteger(byte[] value, boolean isLittleEndian) throws NumberFormatException {
        UnsignedInteger uint = new UnsignedInteger(value, isLittleEndian);

        signedInt = uint.intValue();
    }

    /**
     * Get signed int value
     *
     * @param value String representation of unsigned int
     * @return signed int
     * @throws NumberFormatException Value can't be expressed as signed int
     */
    public static int intValue(String value) throws NumberFormatException {
        return new SignedInteger(value).intValue();
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
     * @return an Exception for invalid scale
     */
    protected static NumberFormatException newNumberFormatException() {
        return new NumberFormatException("Can't be expressed as signed int");
    }

    /**
     * Get value as signed int
     *
     * @return signed int
     */
    public int intValue() {
        return signedInt;
    }
}
