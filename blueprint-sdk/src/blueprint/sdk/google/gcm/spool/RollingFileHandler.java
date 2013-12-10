/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.spool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import blueprint.sdk.google.gcm.GcmResponseDetail;
import blueprint.sdk.util.Validator;

/**
 * Rolls error log into separate files hourly.
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 11.
 */
public class RollingFileHandler extends GcmErrorHandler {
	private static final Logger L = Logger.getLogger(RollingFileHandler.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
	private static final long HOUR = 60 * 60 * 1000;

	private static String errorPath;
	private static PrintWriter errorLog;
	private static long timestamp;

	static {
		errorPath = System.getProperty("GCM_ERROR_PATH");
		if (Validator.isEmpty(errorPath)) {
			L.error("Environment GCM_ERROR_PATH is not set");
		} else {
			createErrorLog();
		}
	}

	private static void createErrorLog() {
		if (errorLog != null) {
			errorLog.close();
		}

		try {
			timestamp = System.currentTimeMillis();
			errorLog = new PrintWriter(new FileWriter(errorPath + "/" + "gcm_error_" + dateFormat.format(new Date())
					+ ".txt", true));
		} catch (IOException e) {
			L.error("Can't create appender for error log", e);
		}
	}

	private static void rollErrorLog() {
		if (System.currentTimeMillis() > timestamp + HOUR) {
			createErrorLog();
		}
	}

	private void logError(String message) {
		synchronized (GcmErrorHandler.class) {
			rollErrorLog();
			errorLog.println(message);
			errorLog.flush();
		}
	}

	@Override
	protected void logGcmError(String regId, GcmResponseDetail detail) {
		logError("{ \"registraion_id\": \"" + regId + "\", \"gcm_error\": \"" + detail.message + "\" }");
	}

	@Override
	protected void logIoError(IOException exIo, String regId) {
		logError("{ \"registraion_id\": \"" + regId + "\", \"io_error\": \"" + exIo.getMessage() + "\" }");
	}
}
