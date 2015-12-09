/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */
package blueprint.sdk.util.buffer;

/**
 * OverFlow occurred in CircularByteBuffer
 *
 * @author Sangmin Lee
 * @since 2008. 12. 1.
 */
public class OverflowException extends RuntimeException {
    private static final long serialVersionUID = -1012404892800531219L;

    private final int dataSize;

    /**
     * Constructor
     *
     * @param dataSize size of byte[] you pushed
     */
    public OverflowException(final int dataSize) {
        super();

        this.dataSize = dataSize;
    }

    public int getNeededCapacity() {
        return dataSize;
    }

    @Override
    public String toString() {
        return "Insufficient buffer size - " + dataSize + " bytes needed";
    }
}
