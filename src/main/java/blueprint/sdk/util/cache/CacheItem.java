/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.cache;

import java.io.Serializable;

/**
 * Item for Cache
 *
 * @param <T> element type
 * @author lempel@gmail.com
 * @since 2013. 6. 28.
 */
public class CacheItem<T> implements Serializable {
    private static final long serialVersionUID = 5248015221521450410L;

    /**
     * actual cache element
     */
    public T element;
    /**
     * last timestamp
     */
    public long timestamp;
}
