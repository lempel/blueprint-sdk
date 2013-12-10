/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.queue;

/**
 * General Interface for Queue
 * 
 * @author Sangmin Lee
 * @since 2013. 12. 11.
 */
public interface Queue<T> {
	/**
	 * Retrieves an element from queue. (blocks if queue is empty)
	 * 
	 * @return queue element or null(interrupted)
	 */
	T take();

	/**
	 * Push an element to queue
	 * 
	 * @param element
	 * @throws NullPointerException
	 *             null element
	 */
	void push(T element);

	/**
	 * Clears queue
	 */
	void clear();

	/**
	 * @return number of elements
	 */
	int size();
}
