package fluent;

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

    private final ThenClause thenClause = new ThenClause();
    private Object uplifted;

    public SelectCase(O origin, V value) {
        super(true, origin, value);
    }

    public SelectCase(V value) {
        super(true, null, value);
    }

    public void setUplifted(Object uplifted) {
        this.uplifted = uplifted;
    }

    public O uplift(Object value) {
        return Optional.ofNullable(origin)
                .filter(o -> o instanceof SelectCase)
                .map(o -> {
                    ((SelectCase) o).setUplifted(value);
                    return o;
                })
                .orElseThrow(() -> new RuntimeException("Cannot uplift from origin"));
    }

    @SafeVarargs
    private boolean boxedIn(V... values) {
        return Arrays.asList(values).contains(boxed);
    }

    @SafeVarargs
    public final SelectCase<O, V> when(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && boxedIn(values),
                        () -> consumer.accept(boxed));
    }

    @SafeVarargs
    public final SelectCase<O, V> breakWhen(Consumer<V> consumer, V... values) {
        return chainWhen(actionAllowed && boxedIn(values),
                        () -> {
                            consumer.accept(boxed);
                            actionAllowed = false;
                        });
    }

    public SelectCase<O, V> whenOther(Consumer<V> consumer) {
        return chainWhen(actionAllowed, () -> consumer.accept(boxed));
    }

    public SelectCase<O, V> whenOtherThrow() {
        return whenOtherThrow(new NoSuchElementException("Actual value doesn't match any of specified by 'when' clauses"));
    }

    public SelectCase<O, V> whenOtherThrow(RuntimeException e) {
        return chainWhen(actionAllowed, () -> { throw e; });
    }

    public SelectCase<O, V> whenRange(V startInclusive, V endExclusive, Consumer<V> consumer) {
        return chainWhen(actionAllowed && boxed instanceof Number,
                        () -> {
                            double actual = ((Number) boxed).doubleValue(),
                                   start = ((Number) startInclusive).doubleValue(),
                                   end = ((Number) endExclusive).doubleValue();
                            if(start >= end) {
                                double buf = start;
                                start = end;
                                end = buf;
                            }
                            if(start <= actual && actual < end) {
                                consumer.accept(boxed);
                            }
                        });
    }

    public Object retrieve() {
        //или value
        return uplifted;
    }

    /*public Optional<?> optional() {
        return Optional.ofNullable(uplifted);
    }*/

    @SafeVarargs
    public final ThenClause when(V... values) {
        actionAllowed = boxedIn(values); //ToDo? не стоит менять так
        return thenClause;
    }

    //Еще для boolean + whenRange
    public ThenClause when(Predicate<V> valuePredicate) {
        valuePredicate.test(boxed);
        return thenClause;
    }

    public final ThenClause whenNull() {
        return thenClause;
    }

    public class ThenClause {

        ThenClause() { }

        //Аналог проваливания ветвей в свиче
        public SelectCase<O, V> pass(Consumer<V> consumer) {
            return chainWhen(actionAllowed, () -> consumer.accept(boxed));
        }

        public O passThenBack(Consumer<V> consumer) {
            consumer.accept(boxed);
            return uplift(boxed);
        }

        //Аналог ветви вместе с break
        public SelectCase<O, V> block(Consumer<V> consumer) {
            if (actionAllowed) {
                consumer.accept(boxed);
            }
            actionAllowed = false;
            return SelectCase.this;
        }

        //Аналог yield
        public SelectCase<O, V> save(V other) {
            boxed = other;
            return SelectCase.this;
        }

        public O saveThenBack(V other) {
            return uplift(other);
        }

        public <U> SelectCase<SelectCase<O, V>, U> mapThenSelect(Function<? super V, ? extends U> mapper) {
            return new SelectCase<>(SelectCase.this, mapper.apply(boxed));
        }

    }
}
