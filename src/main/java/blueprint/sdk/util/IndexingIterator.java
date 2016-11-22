package blueprint.sdk.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator with index
 *
 * @author lempel@gmail.com
 * @since 2016. 11. 22
 */
public class IndexingIterator<T> implements Iterator {
    private Iterator<T> iterator;
    private int index;

    public IndexingIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        T result;

        try {
            result = iterator.next();
        } catch (NoSuchElementException e) {
            throw e;
        }

        return result;
    }

    /**
     * @return current index
     */
    public int index() {
        return index;
    }
}
