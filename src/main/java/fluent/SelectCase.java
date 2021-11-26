package fluent;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Created on 26.11.2021 by
 *
 * @author alexandrov
 */
public class SelectCase<V> extends Cascade<SelectCase<V>, V> {

    SelectCase(boolean actionAllowed, SelectCase<V> origin) {
        super(actionAllowed, SelectCase::new, origin, null);
    }

    SelectCase(V value) {
        super(true, SelectCase::new, null, value);
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

    public SelectCase<V> whenRange(V startInclusive, V endExclusive, Consumer<V> consumer) {
        return chainWhen(actionAllowed && value instanceof Number,
                        () -> {
                            double actual = ((Number) value).doubleValue(),
                                   start = ((Number) startInclusive).doubleValue(),
                                   end = ((Number) endExclusive).doubleValue();
                            if(start >= end) {
                                double buf = start;
                                start = end;
                                end = buf;
                            }
                            if(start <= actual && actual < end) {
                                consumer.accept(value);
                            }
                        });
    }

}
