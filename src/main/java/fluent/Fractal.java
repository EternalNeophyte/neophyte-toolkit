package fluent;

import support.Chaining;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.nonNull;

/**
 * Created on 23.11.2021 by
 *
 * @author alexandrov
 */
public class Fractal<T extends Fractal<T, V>, V> implements Chaining<T> {

    BiFunction<Boolean, T, T> expander;
    T origin;
    V value;
    boolean valueUplifted;
    boolean actionAllowed;

    Fractal(boolean actionAllowed, T origin, BiFunction<Boolean, T, T> expander) {
        this.expander = expander;
        this.origin = origin;
        this.value = null;
        this.valueUplifted = false;
        this.actionAllowed = actionAllowed;
    }

    T withUplifted(V value) {
        return chain(() -> {
            this.value = value;
            this.valueUplifted = true;
        });
    }

    T expandSelf(boolean actionAllowed, boolean nextCondition) {
        return exchangeWhen(actionAllowed, expander.apply(nextCondition, (T) this));
    }

    T back() {
        return nonNull(origin) ? origin.withUplifted(value) : (T) this;
    }

    T thenBreak(boolean condition, V value) {
        when(condition, () -> this.value = value);
        return back();
    }

    V retrieve(V other) {
        return valueUplifted
                    ? requireNonNull(value)
                    : null; //ToDo придумать что-нибудь с дефолтным значением типа byDefault
    }

    V thenYield(boolean condition, V value) {
        return condition
                ? requireNonNull(value)
                : retrieve(value);
    }

    Optional<V> thenOptional(boolean condition, V value) {
        return Optional.ofNullable(thenYield(condition, value));
    }

}
