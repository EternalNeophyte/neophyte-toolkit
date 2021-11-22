package fluent;

import support.Chaining;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public class TernaryOp<T> implements Chaining<TernaryOp<T>> {

    private boolean routeCondition;
    TernaryOp<T> origin;
    private T result;

    public TernaryOp(boolean routeCondition, TernaryOp<T> origin) {
        this.routeCondition = routeCondition;
        this.origin = origin;
    }

    TernaryOp(boolean routeCondition) {
        this(routeCondition, null);
    }

    public TernaryOp<T> yes(T value) {
        return chainingIf(routeCondition, () -> result = value);
    }

    public TernaryOp<T> yes(Supplier<T> valueSupplier) {
        return chainingIf(routeCondition, () -> result = valueSupplier.get());
    }

    public T no(T value) {
        return routeCondition ? Objects.requireNonNull(result) : value;
    }

    public T no(Supplier<T> valueSupplier) {
        return routeCondition ? Objects.requireNonNull(result) : valueSupplier.get();
    }

    public TernaryOp<T> yesThenAsk(boolean condition) {
        return routeCondition ? new TernaryOp<>(condition, this) : this;
    }

    public TernaryOp<T> yesThenAsk(BooleanSupplier condition) {
        return chainingIf(routeCondition, () -> routeCondition = condition.getAsBoolean());
    }

    public TernaryOp<T> noThenAsk(boolean condition) {
        return chainingIf(!routeCondition, () -> routeCondition = condition);
    }

    public TernaryOp<T> noThenAsk(BooleanSupplier condition) {
        return chainingIf(!routeCondition, () -> routeCondition = condition.getAsBoolean());
    }

}
