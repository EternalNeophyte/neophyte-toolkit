package fluent;

import support.Chaining;

import static java.util.Objects.requireNonNull;

/**
 *
 * @param <O> origin type
 * @param <T> stands for 'this'
 * @param <V> type of value boxed in
 */
public abstract class Polymorph<O, T extends Polymorph<O, T, V>, V> implements Chaining<T> {

    boolean actionAllowed;
    O origin;
    V boxed;

    public Polymorph(boolean actionAllowed, O origin, V boxed) {
        this.actionAllowed = actionAllowed;
        this.origin = origin;
        this.boxed = boxed;
    }

    T rebox(V boxed) {
        return chain(() -> this.boxed = requireNonNull(boxed));
    }

    T reboxWhenAllowed(V boxed) {
        return chainWhen(actionAllowed, () -> this.boxed = requireNonNull(boxed));
    }
}
