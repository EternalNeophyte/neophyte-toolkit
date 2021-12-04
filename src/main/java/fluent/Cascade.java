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

    public Cascade(boolean actionAllowed, T origin, V boxed, BiFunction<Boolean, T, T> expander) {
        super(actionAllowed, origin, boxed);
        this.expander = expander;
    }

    T expandSelf(boolean actionAllowed, boolean nextCondition) {
        return swapWhen(actionAllowed, expander.apply(nextCondition, (T) this));
    }

    T thenBlock(boolean condition, V other) {
        return swap(origin, o -> o.rebox(condition ? other : boxed));
    }

    V thenUnbox(boolean condition, V other) {
        return condition ? other : boxed;
    }

    Optional<V> thenOptional(boolean condition, V value) {
        return Optional.ofNullable(thenUnbox(condition, value));
    }

}
