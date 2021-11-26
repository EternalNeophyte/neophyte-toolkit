package fluent;

import support.Chaining;

import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * Created on 23.11.2021 by
 *
 * @author alexandrov
 */
public class Cascade<T extends Cascade<T, V>, V> implements Chaining<T> {

    boolean actionAllowed;
    BiFunction<Boolean, T, T> expander;
    T origin;
    V value;

    public Cascade(boolean actionAllowed, BiFunction<Boolean, T, T> expander, T origin, V value) {
        this.actionAllowed = actionAllowed;
        this.expander = expander;
        this.origin = origin;
        this.value = value;
    }

    T updated(V value) {
        return chain(() -> this.value = value);
    }

    T expandSelf(boolean actionAllowed, boolean nextCondition) {
        return exchangeWhen(actionAllowed, expander.apply(nextCondition, (T) this));
    }

    T back() {
        return nonNull(origin) ? origin.updated(value) : (T) this;
    }

    T thenBreak(boolean condition, V value) {
        if(condition) {
            this.value = value;
        }
        return back();
    }

    V thenYield(boolean condition, V other) {
        return condition ? requireNonNull(other) : value;
    }

    Optional<V> thenOptional(boolean condition, V value) {
        return Optional.ofNullable(thenYield(condition, value));
    }

}
