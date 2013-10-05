/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Bad, bad, bad...<br>
 * But it helps sometimes.<br>
 * 
 * @author Sangmin Lee
 * @since 2007. 07. 31
 */
public class GlobalContext extends HashMap<String, Object> {
	private static final long serialVersionUID = 3099255535794184917L;

	/** Singleton */
	private static GlobalContext context = new GlobalContext();

	public static GlobalContext getInstance() {
		return context;
	}

	@SuppressWarnings("unchecked")
	public GlobalContext clone() {
		GlobalContext result = new GlobalContext();

		result.putAll((Map<? extends String, ? extends Object>) super.clone());

		return result;
	}
}
