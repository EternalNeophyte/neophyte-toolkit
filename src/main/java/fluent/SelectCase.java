package fluent;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.*;

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

    public SelectCase(O origin, V value, boolean notBlocked) {
        super(true, origin, value);
        this.space = new ActionSpace();
        this.uplifted = null;
        this.notBlocked = notBlocked;
        this.notMatched = true;
    }

    public SelectCase(O origin, V value) {
        this(origin, value, true);
    }

    public SelectCase(V value) {
        this(null, value);
    }

    private void match() {
        if(actionAllowed) {
            notMatched = false;
        }
    }

    private boolean allowed() {
        return notBlocked && actionAllowed;
    }

    private O uplift(Object value, boolean notBlocked) {
        return Optional.ofNullable(origin)
                .filter(o -> o instanceof SelectCase)
                .map(o -> {
                    SelectCase sc = (SelectCase) o;
                    if(sc.notBlocked) {
                        sc.notBlocked = notBlocked;
                    }
                    if(actionAllowed) {
                        sc.uplifted = value;
                    }
                    return o;
                })
                .orElseThrow(UpliftNotPossibleException::new);
    }

    private ActionSpace toSpace(BooleanSupplier allowanceSupplier) {
        actionAllowed = allowanceSupplier.getAsBoolean();
        if(actionAllowed) {
            notMatched = false;
        }
        return space;
    }

    public Object unbox() {
        return uplifted;
    }

    public Optional<?> optional() {
        return Optional.ofNullable(uplifted);
    }

    @SafeVarargs
    public final ActionSpace when(V... values) {
        actionAllowed = Arrays.asList(values).contains(boxed);
        match();
        return space;
    }

    public ActionSpace when(Predicate<V> valuePredicate) {
        actionAllowed = valuePredicate.test(boxed);
        match();
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
        if(nonNull(startInclusive) && nonNull(endExclusive)) {
            if(boxed instanceof Number) {
                double actual = ((Number) boxed).doubleValue(),
                        start = ((Number) startInclusive).doubleValue(),
                        end = ((Number) endExclusive).doubleValue();
                actionAllowed = start < end
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
            if(actionAllowed) {
                notMatched = false;
            }
        }
        else {
            actionAllowed = false;
        }
        return space;
    }

    public class ActionSpace {

        ActionSpace() { }

        public <U> SelectCase<SelectCase<O, V>, U> mapThenSelect(Function<? super V, ? extends U> mapper) {
            return new SelectCase<>(SelectCase.this, mapper.apply(boxed), allowed());
        }

        public <U> SelectCase<O, V> mapThenSave(Function<? super V, ? extends U> boxMapper) {
            return chainWhen(allowed(), () -> uplifted = boxMapper.apply(boxed));
        }

        public SelectCase<O, V> save(V other) {
            return chainWhen(allowed(), () -> uplifted = other);
        }

        public SelectCase<O, V> save(UnaryOperator<V> boxOp) {
            return chainWhen(allowed(), () -> uplifted = boxOp.apply(boxed));
        }

        public SelectCase<O, V> pass(Consumer<? super V> consumer) {
            return chainWhen(allowed(), () -> consumer.accept(boxed));
        }

        public SelectCase<O, V> block(Consumer<? super V> consumer) {
            return chainWhen(allowed() && nonNull(consumer),
                            () -> {
                                consumer.accept(boxed);
                                notBlocked = false;
                            });
        }

        public SelectCase<O, V> exception(RuntimeException e) {
            return chainWhen(allowed(), () -> {
                throw e;
            });
        }

        public SelectCase<O, V> exception() {
            return exception(new NoSuchElementException("Actual value doesn't match any of specified by 'when' clauses"));
        }

        public O saveThenBack(V other) {
            return uplift(other, true);
        }

        public O passThenBack(Consumer<V> consumer) {
            pass(consumer);
            return uplift(uplifted, true);
        }

        public O blockThenBack(Consumer<V> consumer) {
            block(consumer);
            return uplift(uplifted, false);
        }
    }

}
