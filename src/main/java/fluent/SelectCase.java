package fluent;

import support.Chaining;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created on 26.11.2021 by
 *
 * @author alexandrov
 */
public class SelectCase<O, V> extends Polymorph<O, SelectCase<O, V>, V> {

    ThenClause thenClause = new ThenClause();
    Object uplifted;

    public void setUplifted(Object uplifted) {
        this.uplifted = uplifted;
    }

    public SelectCase(boolean actionAllowed, O origin, V value) {
        super(actionAllowed, origin, value);
    }

    public SelectCase(V value) {
        super(true, null, value);
    }

    public O uplift(Object value) {
        if(origin != null && origin instanceof SelectCase) {
            ((SelectCase) origin).setUplifted(value);
        }
        return origin;
    }

    /*SelectCase(boolean actionAllowed, SelectCase<V> origin, V value) {
        super(actionAllowed, null, origin, value);
    }

    SelectCase(V value) {
        super(false, null, null, value);
    }*/

    @SafeVarargs
    private boolean equalsAny(V... values) {
        return Arrays.asList(values).contains(value);
    }

    @SafeVarargs
    public final SelectCase<O, V> when(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && equalsAny(values),
                        () -> consumer.accept(value));
    }

    @SafeVarargs
    public final SelectCase<O, V> breakWhen(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && equalsAny(values),
                        () -> {
                            consumer.accept(value);
                            actionAllowed = false;
                        });
    }

    public SelectCase<O, V> whenOther(Consumer<V> consumer) {
        return chainWhen(actionAllowed, () -> consumer.accept(value));
    }

    public SelectCase<O, V> whenOtherThrow() {
        return whenOtherThrow(new NoSuchElementException("Actual value doesn't match any of specified by 'when' clauses"));
    }

    public SelectCase<O, V> whenOtherThrow(RuntimeException e) {
        return chainWhen(actionAllowed, () -> { throw e; });
    }

    public SelectCase<O, V> whenRange(V startInclusive, V endExclusive, Consumer<V> consumer) {
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

    public Object retrieve() {
        //или value
        return uplifted;
    }

    public Optional<?> optional() {
        return Optional.ofNullable(uplifted);
    }

    @SafeVarargs
    public final ThenClause when(V... values) {
        actionAllowed = equalsAny(values); //ToDo? не стоит менять так
        return thenClause;
    }

    //Еще для boolean + whenRange
    public ThenClause when(Predicate<V> valuePredicate) {
        valuePredicate.test(value);
        return thenClause;
    }

    public final ThenClause whenNull() {
        return thenClause;
    }

    public class ThenClause {

        ThenClause() { }

        //Аналог проваливания ветвей в свиче
        public SelectCase<O, V> pass(Consumer<V> consumer) {
            return chainWhen(actionAllowed, () -> consumer.accept(value));
        }

        public O passThenBack(Consumer<V> consumer) {
            consumer.accept(value);
            return uplift(value);
        }

        //Аналог ветви вместе с break
        public SelectCase<O, V> block(Consumer<V> consumer) {
            if (actionAllowed) {
                consumer.accept(value);
            }
            actionAllowed = false;
            return SelectCase.this;
        }

        //Аналог yield
        public SelectCase<O, V> save(V other) {
            value = other;
            return SelectCase.this;
        }

        public O saveThenBack(V other) {
            return uplift(other);
        }

        public <U> SelectCase<SelectCase<O, V>, U> mapThenSelect(Function<? super V, ? extends U> mapper) {
            return new SelectCase<>(actionAllowed, SelectCase.this, mapper.apply(value));
        }

    }
}
