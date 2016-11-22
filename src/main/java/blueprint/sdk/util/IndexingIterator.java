package blueprint.sdk.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
