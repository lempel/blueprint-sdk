/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.queue;

/**
 * General Interface for Queue
 *
 * @author lempel@gmail.com
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
     * @param element element to push
     * @throws NullPointerException null element
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
