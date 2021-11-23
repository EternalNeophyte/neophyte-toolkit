package support;

import java.util.function.Supplier;

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

    default T chainWhenOrElse(boolean condition, Runnable main, Runnable alternative) {
        return condition ? chain(main) : chain(alternative);
    }

    default T exchangeWhen(boolean condition, T other) {
        return condition && other != null ? other : (T) this ;
    }

    default T exchangeWhen(boolean condition, Supplier<T> other) {
        return condition  ? other.get() : (T) this;
    }
}
