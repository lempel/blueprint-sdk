/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Iterator with index
 *
 * @author lempel@gmail.com
 * @since 2016. 11. 22
 */
public class IndexingIterator<E> implements Iterator<E> {
    private Iterator<E> iterator;
    private int index = -1;

    public IndexingIterator(Iterable<E> iterble) {
        this.iterator = iterble.iterator();
    }

    public IndexingIterator(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    public <T> IndexingIterator<T> wrap(Iterable<T> iterable) {
        return new IndexingIterator<T>(iterable);
    }

    public <T> IndexingIterator<T> wrap(Iterator<T> iterator) {
        return new IndexingIterator<T>(iterator);
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
            index++;
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

    public void forEach(BiConsumer<Integer, E> action) {
        Objects.requireNonNull(action);
        while (hasNext()) {
            E item = next();
            action.accept(index(), item);
        }
    }
}
