/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.filesystem;

import java.io.IOException;

/**
 * Abstract File System
 *
 * @author lempel@gmail.com
 * @since 2014. 4. 21.
 */
public abstract class FileSystem {
    /**
     * See if specified file exists or not
     *
     * @param path file path
     * @return true if file exists
     * @throws IOException Can't access given path
     */
    public abstract boolean exists(String path) throws IOException;

    /**
     * Delete a file
     *
     * @param path file path
     * @return true if and only if the file or directory is successfully deleted
     * @throws IOException Can't access given path
     */
    public abstract boolean deleteFile(String path) throws IOException;

    /**
     * Rename a file.<br/>
     * <br/>
     * <b>Usually, renaming a file would be done asynchronously by OS Kernel.</b><br/>
     *
     * @param orgPath file path
     * @param newPath new path
     * @return true if and only if the renaming succeeded
     * @throws IOException Can't access given path
     */
    public abstract boolean renameFile(String orgPath, String newPath) throws IOException;

    /**
     * Read file contents
     *
     * @param path file path
     * @return File's contents
     * @throws IOException Can't access given path
     */
    public abstract byte[] readFile(String path) throws IOException;

    /**
     * Write given contents to a file
     *
     * @param path     file path
     * @param contents file's contents
     * @param append   true for append to current contents, false for create a new
     *                 file
     * @throws IOException Can't access given path
     */
    public abstract void writeToFile(String path, byte[] contents, boolean append) throws IOException;

    /**
     * Dispose all resources
     */
    public abstract void dispose();
}
