package fluent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created on 26.11.2021 by
 *
 * @author alexandrov
 */
public class SelectCase<O, V> extends Polymorph<O, SelectCase<O, V>, V> {

    private final ActionSpace space;
    private Object uplifted;
    private boolean notBlocked;
    private boolean notMatched;

    public SelectCase(O origin, V value) {
        super(true, origin, value);
        this.space = new ActionSpace();
        this.uplifted = null;
        this.notBlocked = true;
        this.notMatched = true;
    }

    public SelectCase(V value) {
        this(null, value);
    }

    private O uplift(Object value) {
        return Optional.ofNullable(origin)
                .filter(o -> o instanceof SelectCase)
                .map(o -> {
                    if(actionAllowed) {
                        ((SelectCase) o).uplifted = value;
                    }
                    return o;
                })
                .orElseThrow(UpliftNotPossibleException::new);
    }

    @SafeVarargs
    private boolean boxedIn(V... values) {
        boolean boxedIn = Arrays.asList(values).contains(boxed);
        if(!boxedIn) {
            notMatched = false; //ToDo вынести в отдельную ф-ю
        }
        return boxedIn;
    }

    @SafeVarargs
    public final ActionSpace when(V... values) {
        actionAllowed = boxedIn(values);
        return space;
    }

    public ActionSpace when(Predicate<V> valuePredicate) {
        actionAllowed = valuePredicate.test(boxed);
        return space;
    }

    public ActionSpace whenNull() {
        actionAllowed = isNull(boxed);
        return space;
    }

    public ActionSpace whenOther() {
        actionAllowed = notMatched;
        return space;
    }

    public ActionSpace whenRange(V startInclusive, V endExclusive) {
        if(actionAllowed && nonNull(startInclusive)
                         && nonNull(endExclusive)) {
            if(boxed instanceof Number) {
                double actual = ((Number) boxed).doubleValue(),
                        start = ((Number) startInclusive).doubleValue(),
                        end = ((Number) endExclusive).doubleValue();
                actionAllowed = start > end
                        ? start <= actual && actual < end
                        : start > actual && actual >= end;
            }
            else if(boxed instanceof Comparable) {
                Comparable actual = (Comparable) boxed,
                        start = (Comparable) startInclusive,
                        end = (Comparable) endExclusive;
                actionAllowed = start.compareTo(end) < 0
                        ? start.compareTo(actual) <= 0 && actual.compareTo(end) < 0
                        : start.compareTo(actual) > 0 && actual.compareTo(end) >= 0;
            }
        }
        return space;
    }

    public Object unbox() {
        return uplifted;
    }

    public Optional<?> optional() {
        return Optional.ofNullable(uplifted);
    }

    public class ActionSpace {

        ActionSpace() { }

        public <U> SelectCase<SelectCase<O, V>, U> mapThenSelect(Function<? super V, ? extends U> mapper) {
            return new SelectCase<>(SelectCase.this, mapper.apply(boxed));
        }

        public SelectCase<O, V> pass(Consumer<? super V> consumer) {
            return chainWhen(actionAllowed, () -> consumer.accept(boxed));
        }

        public SelectCase<O, V> block(Consumer<? super V> consumer) {
            return chainWhen(actionAllowed && notBlocked && nonNull(consumer),
                    () -> {
                        consumer.accept(boxed);
                        notBlocked = false;
                    });
        }

        public SelectCase<O, V> save(V other) {
            return chainWhen(actionAllowed && notBlocked, () -> boxed = other);
        }

        public SelectCase<O, V> save(UnaryOperator<V> op) {
            return chainWhen(actionAllowed && notBlocked, () -> boxed = op.apply(boxed));
        }

        public SelectCase<O, V> exception(RuntimeException e) {
            return chainWhen(actionAllowed && notMatched && notBlocked, () -> { throw e; });
        }

        public SelectCase<O, V> exception() {
            return exception(new NoSuchElementException("Actual value doesn't match any of specified by 'when' clauses"));
        }

        public O blockThenBack(Consumer<V> consumer) {
            block(consumer);
            return uplift(boxed);
        }

        public O passThenBack(Consumer<V> consumer) {
            pass(consumer);
            return uplift(boxed);
        }

        public O saveThenBack(V other) {
            return uplift(other);
        }
    }
}
