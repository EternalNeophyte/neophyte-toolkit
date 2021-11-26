package support;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public interface Chaining<T extends Chaining<T>> {

    default void when(boolean condition, Runnable action) {
        if(condition) {
            action.run();
        }
    }

    default T chain(Runnable action) {
        action.run();
        return (T) this;
    }

    default T chainWhen(boolean condition, Runnable action) {
        return condition ? chain(action) : (T) this;
    }

    default T chainWhen(BooleanSupplier condition, Runnable action) {
        return condition.getAsBoolean() ? chain(action) : (T) this;
    }

    default T chainWhenOrElse(boolean condition, Runnable main, Runnable alternative) {
        return condition ? chain(main) : chain(alternative);
    }

    default T exchangeWhen(boolean condition, T other) {
        return condition && nonNull(other) ? other : (T) this;
    }

    default T exchangeWhen(boolean condition, Supplier<T> other) {
        return condition && nonNull(other) ? other.get() : (T) this;
    }
}
