package blueprint.sdk.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator with index
 *
 * @author lempel@gmail.com
 * @since 2016. 11. 22
 */
public class IndexingIterator<E> implements Iterator<E> {
    private Iterator<E> iterator;
    private int index;

    public IndexingIterator(Iterable<E> iterble) {
        this.iterator = iterble.iterator();
    }

    public IndexingIterator(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        E result;

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
