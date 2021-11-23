package fluent;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public class TernaryOp<V> extends Fractal<TernaryOp<V>, V> {

    public TernaryOp(boolean actionAllowed, TernaryOp<V> origin) {
        super(actionAllowed, origin);
    }

    TernaryOp(boolean actionAllowed) {
        super(actionAllowed);
    }

    public TernaryOp<V> yes(V value) {
        return chainWhen(actionAllowed, () -> this.value = value);
    }

    public TernaryOp<V> yes(Supplier<V> value) {
        return chainWhen(actionAllowed, () -> this.value = value.get());
    }

    public V no(V other) {
        return retrieve(other);
    }

    public V no(Supplier<V> other) {
        return retrieve(other);
    }

    public TernaryOp<V> yesThenAsk(boolean condition) {
        return expandSelfWhen(actionAllowed, condition);
    }

    public TernaryOp<V> yesThenAsk(BooleanSupplier condition) {
        return chainWhen(actionAllowed, () -> actionAllowed = condition.getAsBoolean());
    }

    public TernaryOp<V> noThenAsk(boolean condition) {
        return chainWhen(!actionAllowed, () -> actionAllowed = condition);
    }

    public TernaryOp<V> noThenAsk(BooleanSupplier condition) {
        return chainWhen(!actionAllowed, () -> actionAllowed = condition.getAsBoolean());
    }

}
