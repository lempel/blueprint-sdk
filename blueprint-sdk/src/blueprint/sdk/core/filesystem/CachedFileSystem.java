/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.filesystem;

import blueprint.sdk.util.cache.Cache;

import java.io.IOException;

/**
 * Cached File System.<br/>
 * Cached items will and only be evicted by TTL(Time-To-Live).<br/>
 *
 * @author Sangmin Lee
 * @since 2014. 4. 23.
 */
public class CachedFileSystem extends ConcurrentFileSystem {
    /**
     * Cached file contents
     */
    @SuppressWarnings("WeakerAccess")
    protected final Cache<byte[]> cache;

    /**
     * @param ttl TTL in millisecond
     */
    public CachedFileSystem(long ttl) {
        super();

        cache = new Cache<>(5, ttl, true);
    }

    @Override
    public boolean deleteFile(String path) {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        synchronized (cache) {
            cache.remove(path);
        }

        return super.deleteFile(path);
    }

    @Override
    public boolean renameFile(String orgPath, String newPath) {
        boolean result = super.renameFile(orgPath, newPath);

        if (result) {
            synchronized (cache) {
                byte[] contents = cache.remove(orgPath);
                cache.put(newPath, contents);
            }
        }

        return result;
    }

    @Override
    public byte[] readFile(String path) throws IOException {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        byte[] result = cache.get(path);

        if (result == null) {
            result = super.readFile(path);
            synchronized (cache) {
                cache.put(path, result);
            }
        }

        return result;
    }

    @Override
    public void writeToFile(String path, byte[] contents, boolean append) throws IOException {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        super.writeToFile(path, contents, append);

        synchronized (cache) {
            if (append) {
                byte[] orgContents = cache.get(path);
                byte[] newContents = new byte[orgContents.length + contents.length];
                System.arraycopy(orgContents, 0, newContents, 0, orgContents.length);
                System.arraycopy(contents, 0, newContents, orgContents.length, contents.length);

                cache.put(path, newContents);
            } else {
                cache.put(path, contents);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        cache.dispose();
    }
}
