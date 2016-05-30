/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.filesystem;

import java.io.*;

/**
 * Generic OS dependent File System
 *
 * @author lempel@gmail.com
 * @since 2014. 4. 21.
 */
public class GenericFileSystem extends FileSystem {
    @Override
    public boolean exists(String path) {
        return new File(path).exists();
    }

    @Override
    public boolean deleteFile(String path) {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        boolean result = false;
        File target = new File(path);
        if (target.exists()) {
            result = target.delete();
        }
        return result;
    }

    @Override
    public boolean renameFile(String orgPath, String newPath) {
        if (orgPath == null || newPath == null) {
            throw new NullPointerException("at least one of specified path is null");
        }

        boolean result = false;

        if (!orgPath.equals(newPath)) {
            File target = new File(orgPath);
            result = target.renameTo(new File(newPath));
        }

        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public byte[] readFile(String path) throws IOException {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        byte[] result = null;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            result = new byte[fis.available()];
            fis.read(result);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return result;
    }

    @Override
    public void writeToFile(String path, byte[] contents, boolean append) throws IOException {
        if (path == null) {
            throw new NullPointerException("specified path is null");
        }

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(path, append));
            bos.write(contents);
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    @Override
    public void dispose() {
        // NO-Op
    }
}
