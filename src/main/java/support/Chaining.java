package support;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public interface Chaining<T extends Chaining<T>> {

    default T chaining(Runnable action) {
        action.run();
        return (T) this;
    }

    default T chainingIf(boolean condition, Runnable action) {
        return condition ? chaining(action) : (T) this;
    }
}
