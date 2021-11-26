package fluent;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created on 26.11.2021 by
 *
 * @author alexandrov
 */
public class SelectCase<V> extends Fractal<SelectCase<V>, V>  {

    SelectCase(boolean actionAllowed, SelectCase<V> origin, BiFunction<Boolean, SelectCase<V>, SelectCase<V>> expander) {
        super(actionAllowed, origin, expander);
    }
    //ToDo
    SelectCase(V value) {
        this.value = value;
    }

    @SafeVarargs
    private boolean equalsOneOf(V... values) {
        return Arrays.asList(values).contains(value);
    }

    @SafeVarargs
    public final SelectCase<V> when(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && equalsOneOf(values),
                        () -> consumer.accept(value));
    }

    @SafeVarargs
    public final SelectCase<V> breakWhen(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && equalsOneOf(values),
                        () -> {
                            consumer.accept(value);
                            actionAllowed = false;
                        });
    }

    public SelectCase<V> whenOther(Consumer<V> consumer) {
        return chainWhen(actionAllowed, () -> consumer.accept(value));
    }

    public SelectCase<V> whenOtherThrow() {
        return whenOtherThrow(new NoSuchElementException("Actual value doesn't match any of specified by 'when' clauses"));
    }

    public SelectCase<V> whenOtherThrow(RuntimeException e) {
        return chainWhen(actionAllowed, () -> {
            throw e;
        });
    }

    public SelectCase<V> whenRange(V startInclusive, V endExclusive, Consumer<V> action) {
        return chainWhen(actionAllowed && value instanceof Number,
                        () -> {
                            int valueInt = ((Number)value).intValue(),
                                startInclusiveInt = ((Number)startInclusive).intValue(),
                                endExclusiveInt = ((Number)endExclusive).intValue();
                            //ToDo сравнения
                        });
    }

}
