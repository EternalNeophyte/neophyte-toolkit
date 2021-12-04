package support;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.nonNull;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public interface Chaining<T extends Chaining<T>> {

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

    default T chainWhenOrElse(BooleanSupplier condition, Runnable main, Runnable alternative) {
        return condition.getAsBoolean() ? chain(main) : chain(alternative);
    }

    default T swap(T other) {
        return nonNull(other) ? other : (T) this;
    }

    default T swap(T other, UnaryOperator<T> otherMapper) {
        return nonNull(other) ? otherMapper.apply(other) : (T) this;
    }

    default T swapWhen(boolean condition, T other) {
        return condition && nonNull(other) ? other : (T) this;
    }

    default T swapWhen(BooleanSupplier condition, T other) {
        return condition.getAsBoolean() ? requireNonNull(other) : (T) this;
    }

    default T swapWhen(boolean condition, Supplier<T> other) {
        return condition ? requireNonNull(other).get() : (T) this;
    }

    default T swapWhen(BooleanSupplier condition, Supplier<T> other) {
        return condition.getAsBoolean() ? requireNonNull(other).get() : (T) this;
    }

}
