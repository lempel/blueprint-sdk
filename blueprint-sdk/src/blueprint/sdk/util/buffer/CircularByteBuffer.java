/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.buffer;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * If incremental is set, buffer's size will be increase automatically.<br>
 * If incremental is not set and overflowCheck is set, throws OverflowException.<br>
 * And if both incremental and overflowCheck are not set, old bytes will be discarded (circular buffer).<br>
 * <br>
 * <b>This class is thread safe.</b><br>
 *
 * @author Sangmin Lee
 * @since 2008. 11. 24.
 */
public class CircularByteBuffer {
    private final boolean incremental;
    private final boolean overflowCheck;
    private ByteBuffer buffer = null;
    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public CircularByteBuffer(final int capacity, final boolean incremental, final boolean overflowCheck) {
        this.incremental = incremental;
        this.overflowCheck = overflowCheck;
        allocate(capacity);
    }

    private void allocate(final int capacity) {
        buffer = ByteBuffer.allocate(capacity);
    }

    /**
     * push byte[] to buffer
     *
     * @param data
     * @throws IllegalArgumentException invalid start/end index
     */
    public void push(final byte[] data) {
        push(data, 0, data.length);
    }

    /**
     * push byte[] to buffer
     *
     * @param data
     * @param startIndex
     * @param endIndex
     * @throws IllegalArgumentException invalid start/end index
     */
    public void push(final byte[] data, final int startIndex, final int endIndex) {
        lock.writeLock().lock();
        try {
            if (data == null) {
                throw new NullPointerException("data is null");
            }
            if (endIndex > data.length || startIndex > endIndex) {
                throw new IllegalArgumentException("invalid start/end index - length: "
                        + data.length + ", startIndex: " + startIndex + ", endIndex: " + endIndex);
            }

            int length = endIndex - startIndex;

            if (buffer.remaining() >= length) {
                buffer.put(data, startIndex, length);
            } else if (buffer.remaining() >= length) {
                buffer.position(length);
                buffer.compact();
                buffer.put(data, startIndex, length);
            } else if (incremental) {
                resize(buffer.capacity() + (length - buffer.remaining()));
                buffer.put(data, Math.abs(length - buffer.remaining()), buffer.remaining());
            } else if (overflowCheck) {
                throw new blueprint.sdk.util.buffer.OverflowException(length - buffer.remaining());
            } else if (buffer.capacity() < length) {
                buffer.clear();
                buffer.put(data, Math.abs(length - buffer.capacity()), buffer.capacity());
            } else {
                buffer.position(length);
                buffer.compact();
                buffer.put(data, startIndex, length);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Pops all byte[] from buffer
     *
     * @return
     */
    public byte[] pop() {
        byte[] result = array(true);

        return result;
    }

    /**
     * Unlike pop, just returns buffer's content.
     *
     * @return
     */
    public byte[] array() {
        return array(false);
    }

    private byte[] array(final boolean clear) {
        byte[] result;

        lock.readLock().lock();
        try {
            if (buffer.remaining() > 0) {
                result = new byte[buffer.position()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = buffer.get(i);
                }
            } else {
                result = buffer.array();
            }

            if (clear) {
                buffer.clear();
            }
        } finally {
            lock.readLock().unlock();
        }

        return result;
    }

    public int remaining() {
        return buffer.remaining();
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            buffer.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Buffer can't be shrunk. Only grows bigger.
     *
     * @param capacity
     */
    public void resize(final int capacity) {
        lock.writeLock().lock();

        try {
            if (capacity > buffer.capacity()) {
                byte[] data = array(true);
                allocate(capacity);
                push(data);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getCapacity() {
        return buffer.capacity();
    }

    @Override
    protected void finalize() throws Throwable {
        buffer.clear();
        buffer = null;
        while (lock.getWriteHoldCount() > 0) {
            lock.writeLock().unlock();
        }
        while (lock.getReadLockCount() > 0) {
            lock.readLock().unlock();
        }
        lock = null;

        super.finalize();
    }
}
