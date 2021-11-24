package fluent;

import support.Chaining;

import java.util.Objects;
import java.util.function.BiFunction;
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
    BiFunction<Boolean, T, T> expander;

    Fractal(boolean actionAllowed, T origin, BiFunction<Boolean, T, T> expander) {
        this.actionAllowed = actionAllowed;
        this.origin = origin;
        this.value = null;
        this.expander = expander;
    }

    Fractal(boolean actionAllowed, T origin) {
        this(actionAllowed, origin, origin == null ? null : origin.expander);
    }

    Fractal(boolean actionAllowed) {
        this(actionAllowed, null);
    }

    T expandWhen(boolean actionAllowed, boolean nextCondition) {
        return exchangeWhen(actionAllowed, expander == null ? (T) this : expander.apply(nextCondition, (T)this));
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
