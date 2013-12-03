/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.google.gcm.example;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import blueprint.sdk.google.gcm.GcmResponse;
import blueprint.sdk.google.gcm.GcmSender;

/**
 * Example of GcmSender
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 3.
 */
public class GcmSenderExample {
	private static final Logger L = Logger.getLogger(GcmSenderExample.class);

	public static void main(String[] args) throws MalformedURLException, IOException {
		// set your apiKey
		String apiKey = "XXX";
		// set your registration id
		String regId = "XXX";

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("test", "Message from GcmSenderExample");

		GcmSender sender = new GcmSender(apiKey);
		GcmResponse response = sender.send(regId, map, 10);

		L.info(response.code + " - " + response.message);
	}
}
