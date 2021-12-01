package fluent;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created on 26.11.2021 by
 *
 * @author alexandrov
 */
public class SelectCase<V> extends Cascade<SelectCase<V>, V> {

    ThenClause thenClause = new ThenClause();

    SelectCase(boolean actionAllowed, SelectCase<V> origin, V value) {
        super(actionAllowed, null, origin, value);
    }

    SelectCase(V value) {
        super(false, null, null, value);
    }

    @SafeVarargs
    private boolean equalsAny(V... values) {
        return Arrays.asList(values).contains(value);
    }

    @SafeVarargs
    public final SelectCase<V> when(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && equalsAny(values),
                        () -> consumer.accept(value));
    }

    @SafeVarargs
    public final SelectCase<V> breakWhen(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && equalsAny(values),
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
        return chainWhen(actionAllowed, () -> { throw e; });
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

    @SafeVarargs
    public final <U> SelectCase<U> selectCaseWhen(Function<? super V, ? extends U> mapper, V... values) {
        return new SelectCase<U>(equalsAny(values), (SelectCase)this, mapper.apply(value));
    }

    @SafeVarargs
    public final ThenClause when(V... values) {
        actionAllowed = equalsAny(values); //ToDo? не стоит менять так
        return thenClause;
    }

    public class ThenClause {

        ThenClause() {
        }

        public SelectCase<V> then(Consumer<V> consumer) {
            return chainWhen(actionAllowed, () -> consumer.accept(value));
        }

        public SelectCase<V> breaks(Consumer<V> consumer) {
            if (actionAllowed) {
                consumer.accept(value);
            }
            actionAllowed = false;
            return SelectCase.this;
        }

        public final <U> SelectCase<U> select(Function<? super V, ? extends U> mapper) {
            return new SelectCase<U>(actionAllowed, (SelectCase)SelectCase.this, mapper.apply(value));
        }
    }
}
