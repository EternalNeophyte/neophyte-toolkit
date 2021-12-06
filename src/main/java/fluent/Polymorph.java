package fluent;

import support.Chaining;

import static java.util.Objects.requireNonNull;

/**
 *
 * @param <O> origin type
 * @param <T> stands for 'this'
 * @param <V> type of value stored in
 */
public abstract class Polymorph<O, T extends Polymorph<O, T, V>, V> implements Chaining<T> {

    boolean actionAllowed;
    O origin;
    V value;

    public Polymorph(boolean actionAllowed, O origin, V value) {
        this.actionAllowed = actionAllowed;
        this.origin = origin;
        this.value = value;
    }

    T repack(V boxed) {
        return chain(() -> this.value = requireNonNull(boxed));
    }

    T reboxWhenAllowed(V boxed) {
        return chainWhen(actionAllowed, () -> this.value = requireNonNull(boxed));
    }
}
