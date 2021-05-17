/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.buffer;

/**
 * OverFlow occurred in CircularByteBuffer
 *
 * @author lempel@gmail.com
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
