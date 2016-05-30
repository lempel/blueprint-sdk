/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.util.UUID;

/**
 * Handles filenames.
 *
 * @author lempel@gmail.com
 * @since 2002. 07. 30
 */
@SuppressWarnings("WeakerAccess")
public class FilenameUtil {
    private static final String fileSeparator = System.getProperty("file.separator");

    public static String getClassName(final Object obj) {
        return obj.getClass().getName();
    }

    /**
     * generate a random but not existing file name for given path
     *
     * @param path target path
     * @param ext file extension
     * @return full path
     */
    public static String generateRandomFileName(final String path, final String ext) {
        String actualPath = path;
        if (!actualPath.endsWith(fileSeparator)) {
            actualPath = actualPath + fileSeparator;
        }

        String newExt = ext;
        if (newExt.charAt(0) != '.') {
            newExt = "." + newExt;
        }

        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");

        return actualPath + uuid + newExt;
    }

    public static String extractFileName(final String filePath) {
        String result;
        int pos = filePath.lastIndexOf(fileSeparator);
        if (pos < 0) {
            result = filePath;
        } else {
            result = filePath.substring(pos + 1, filePath.length());
        }
        return result;
    }

    public static String getFileSeparator() {
        return fileSeparator;
    }
}