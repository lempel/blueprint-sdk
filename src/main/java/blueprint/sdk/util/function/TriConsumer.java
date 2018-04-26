/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel76.blogspot.kr
        http://lempel.egloos.com
 */

package blueprint.sdk.util.function;

import java.util.Objects;

/**
 * Consumer with three input arguments
 *
 * @author lempel@gmail.com
 * @since 2016. 11. 22
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);

    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);

        return (l, m, r) -> {
            accept(l, m, r);
            after.accept(l, m, r);
        };
    }
}
