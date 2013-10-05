/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util;

/**
 * Provides common method for graceful shutdown
 * 
 * @author Sangmin Lee
 * @since 2007. 07. 18
 */
public interface Terminatable {
	boolean isValid();

	boolean isTerminated();

	void terminate();
}
