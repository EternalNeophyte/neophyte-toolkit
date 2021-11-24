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
        super(actionAllowed, origin, TernaryOp::new);
    }

    TernaryOp(boolean actionAllowed) {
        super(actionAllowed);
    }

    public TernaryOp<V> yes(V value) {
        return chainWhen(actionAllowed, () -> this.value = value);
    }

    public TernaryOp<V> yes(Supplier<V> value) {
        return yes(value.get());
    }

    public V no(V value) {
        return retrieve(value);
    }

    public V no(Supplier<V> value) {
        return retrieve(value);
    }

    public TernaryOp<V> noAndBreak(V value) {
        when(!actionAllowed, () -> this.value = value);
        return back();
    }

    public TernaryOp<V> noAndBreak(Supplier<V> value) {
        return noAndBreak(value.get());
    }

    public TernaryOp<V> yesThenAsk(boolean condition) {
        return expandWhen(actionAllowed, condition);
        //exchangeWhen(actionAllowed, expander.apply(condition, this));
    }

    public TernaryOp<V> yesThenAsk(BooleanSupplier condition) {
        return expandWhen(actionAllowed, condition.getAsBoolean());
    }

    public TernaryOp<V> noThenAsk(boolean condition) {
        return expandWhen(!actionAllowed, condition);
    }

    public TernaryOp<V> noThenAsk(BooleanSupplier condition) {
        return expandWhen(!actionAllowed, condition.getAsBoolean());
    }

}
