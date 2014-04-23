/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.core.filesystem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Generic OS dependent File System
 * 
 * @author Sangmin Lee
 * @since 2014. 4. 21.
 */
public class GenericFileSystem extends FileSystem {
	@Override
	public boolean exists(String path) {
		return new File(path).exists();
	}

	@Override
	public boolean deleteFile(String path) {
		boolean result = false;
		File target = new File(path);
		if (target.exists()) {
			result = target.delete();
		}
		return result;
	}

	@Override
	public boolean renameFile(String orgPath, String newPath) {
		File target = new File(orgPath);
		return target.renameTo(new File(newPath));
	}

	@Override
	public byte[] readFile(String path) throws IOException {
		byte[] result = null;

		if (exists(path)) {
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
		}

		return result;
	}

	@Override
	public void writeToFile(String path, byte[] contents, boolean append) throws IOException {
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
}
