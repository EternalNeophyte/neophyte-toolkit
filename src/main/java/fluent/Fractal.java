package fluent;

import support.Chaining;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.isNull;

/**
 * Created on 23.11.2021 by
 *
 * @author alexandrov
 */
public class Fractal<T extends Fractal<T, V>, V> implements Chaining<T> {

    boolean actionAllowed;
    T origin;
    V value;

    Fractal(boolean actionAllowed, T origin) {
        this.actionAllowed = actionAllowed;
        this.origin = origin;
        this.value = null;
    }

    Fractal(boolean actionAllowed) {
        this(actionAllowed, null);
    }

    T expandSelfWhen(boolean actionAllowed, boolean thenAllowed) {
        return exchangeWhen(actionAllowed, (T) new Fractal<>(thenAllowed, (T) this));
    }

    T back() {
        return exchangeWhen(isNull(origin), origin);
    }

    V retrieve(V other) {
        return actionAllowed ? requireNonNull(value) : requireNonNull(other);
    }

    V retrieve(Supplier<V> other) {
        return retrieve(other.get());
    }
}
