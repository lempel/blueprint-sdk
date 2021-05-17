/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Recycle byte arrays as many as possible to reduce memory allocations.<br>
 * <b>BEWARE: </b>This pool reduces memory allocations and heap usage variations
 * but total performance could be decreased (due to inevitable locks).<br>
 *
 * @author lempel@gmail.com
 * @since 2009. 1. 20.
 */
public class ByteArrayPool {
    private final int arraySize;
    private final int maxArrays;
    private final List<byte[]> pool;

    /**
     * Constructor
     *
     * @param arraySize each array's size
     * @param maxArrays maximum number of arrays to preserve
     */
    @SuppressWarnings("WeakerAccess")
    protected ByteArrayPool(final int arraySize, final int maxArrays) {
        this.arraySize = arraySize;
        this.maxArrays = maxArrays;
        pool = new ArrayList<>(maxArrays);
    }

    public byte[] newArray() {
        byte[] result;

        synchronized (pool) {
            if (pool.isEmpty()) {
                result = new byte[arraySize];
            } else {
                result = pool.remove(0);
            }
        }

        return result;
    }

    /**
     * Release un-necessary array to pool.
     *
     * @param target array to release
     */
    public void release(final byte[] target) {
        synchronized (pool) {
            if (pool.size() < maxArrays) {
                pool.add(target);
            }
        }
    }
}
