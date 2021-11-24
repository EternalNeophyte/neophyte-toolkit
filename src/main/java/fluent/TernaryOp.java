package fluent;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public class TernaryOp<V> extends Fractal<TernaryOp<V>, V> {

    TernaryOp(boolean actionAllowed, TernaryOp<V> origin) {
        super(actionAllowed, origin, TernaryOp::new);
    }

    TernaryOp(boolean actionAllowed) {
        this(actionAllowed, null);
    }

    public TernaryOp<V> yes(V value) {
        return chainWhen(actionAllowed, () -> this.value = value);
    }

    public TernaryOp<V> yes(Supplier<V> value) {
        return yes(value.get());
    }

    public V no(V value) {
        return actionAllowed
                ? retrieve(value)
                : requireNonNull(value);
    }

    public V no(Supplier<V> value) {
        return no(value.get());
    }

    public TernaryOp<V> yesThenAsk(boolean condition) {
        return expandSelf(actionAllowed, condition);
    }

    public TernaryOp<V> yesThenAsk(BooleanSupplier condition) {
        return expandSelf(actionAllowed, condition.getAsBoolean());
    }

    public TernaryOp<V> noThenAsk(boolean condition) {
        return expandSelf(!actionAllowed, condition);
    }

    public TernaryOp<V> noThenAsk(BooleanSupplier condition) {
        return expandSelf(!actionAllowed, condition.getAsBoolean());
    }

    public TernaryOp<V> yesThenBreak(V value) {
        return thenBreak(actionAllowed, value);
    }

    public TernaryOp<V> yesThenBreak(Supplier<V> value) {
        return thenBreak(actionAllowed, value.get());
    }

    public TernaryOp<V> noThenBreak(V value) {
        return thenBreak(!actionAllowed, value);
    }

    public TernaryOp<V> noThenBreak(Supplier<V> value) {
        return thenBreak(!actionAllowed, value.get());
    }




}
