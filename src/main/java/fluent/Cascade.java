package fluent;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Created on 23.11.2021 by
 *
 * @author alexandrov
 */
public abstract class Cascade<T extends Cascade<T, V>, V> extends Polymorph<T, T, V> {

    BiFunction<Boolean, T, T> expander;

    public Cascade(boolean actionAllowed, T origin, V value, BiFunction<Boolean, T, T> expander) {
        super(actionAllowed, origin, value);
        this.expander = expander;
    }

    T expandSelf(boolean actionAllowed, boolean nextCondition) {
        return swapWhen(actionAllowed, expander.apply(nextCondition, (T) this));
    }

    T thenBack(boolean condition, V other) {
        return swap(origin, o -> o.repack(condition ? other : value));
    }

    V thenYield(boolean condition, V other) {
        return condition ? other : value;
    }

    Optional<V> thenOptional(boolean condition, V value) {
        return Optional.ofNullable(thenYield(condition, value));
    }

}
