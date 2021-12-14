package fluent;

import java.util.Arrays;
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
    private Object boxed;
    private boolean notBlocked;
    private boolean notMatched;

    SelectCase(O origin, V value, boolean notBlocked) {
        super(true, origin, value);
        this.space = new ActionSpace();
        this.boxed = null;
        this.notBlocked = notBlocked;
        this.notMatched = true;
    }

    SelectCase(O origin, V value) {
        this(origin, value, true);
    }

    SelectCase(V value) {
        this(null, value);
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
                        sc.boxed = value;
                    }
                    return o;
                })
                .orElseThrow(UpliftNotPossibleException::new);
    }

    private ActionSpace swapToSpace(BooleanSupplier allowanceSupplier) {
        actionAllowed = allowanceSupplier.getAsBoolean();
        if(actionAllowed) {
            notMatched = false;
        }
        return space;
    }

    @SafeVarargs
    public final ActionSpace when(V... values) {
        return swapToSpace(() -> Arrays.asList(values).contains(value));
    }

    public ActionSpace when(Predicate<V> valuePredicate) {
        return swapToSpace(() -> valuePredicate.test(value));
    }

    public ActionSpace whenNull() {
        return swapToSpace(() -> isNull(value));
    }

    public ActionSpace whenOther() {
        return swapToSpace(() -> notMatched);
    }

    public ActionSpace whenRange(V startInclusive, V endExclusive) {
        return swapToSpace(() -> {
            if(nonNull(startInclusive) && nonNull(endExclusive)) {
                if(value instanceof Number) {
                    double actual = ((Number) value).doubleValue(),
                            start = ((Number) startInclusive).doubleValue(),
                            end = ((Number) endExclusive).doubleValue();
                    return start < end
                            ? start <= actual && actual < end
                            : start > actual && actual >= end;
                }
                else if(value instanceof Comparable) {
                    Comparable actual = (Comparable) value,
                            start = (Comparable) startInclusive,
                            end = (Comparable) endExclusive;
                    return start.compareTo(end) < 0
                            ? start.compareTo(actual) <= 0 && actual.compareTo(end) < 0
                            : start.compareTo(actual) > 0 && actual.compareTo(end) >= 0;
                }
            }
            return false;
        });
    }

    public class ActionSpace {

        ActionSpace() { }

        private <A> boolean allowed(A applier) {
            return notBlocked && actionAllowed && nonNull(applier);
        }

        public SelectCase<O, V> pass(Consumer<? super V> passConsumer) {
            return chainWhen(allowed(passConsumer), () -> passConsumer.accept(value));
        }

        public O passThenBack(Consumer<? super V> passConsumer) {
            pass(passConsumer);
            return uplift(boxed, true);
        }

        public SelectCase<O, V> block(Consumer<? super V> blockConsumer) {
            return chainWhen(allowed(blockConsumer), () -> {
                blockConsumer.accept(value);
                notBlocked = false;
            });
        }

        public O blockThenBack(Consumer<? super V> blockConsumer) {
            block(blockConsumer);
            return uplift(boxed, false);
        }

        public SelectCase<O, V> box(V other) {
            return chainWhen(allowed(other), () -> boxed = other);
        }

        public SelectCase<O, V> box(UnaryOperator<V> boxOp) {
            return chainWhen(allowed(boxOp), () -> boxed = boxOp.apply(value));
        }

        public O boxThenBack(V other) {
            return uplift(other, true);
        }

        public O boxThenBack(UnaryOperator<V> boxOp) {
            return uplift(nonNull(boxOp) ? boxOp.apply(value) : boxed, true);
        }

        public <U> SelectCase<O, V> map(Function<? super V, ? extends U> boxMapper) {
            return chainWhen(allowed(boxMapper), () -> boxed = boxMapper.apply(value));
        }

        public <U> SelectCase<SelectCase<O, V>, U> mapThenSelect(Function<? super V, ? extends U> boxMapper) {
            return new SelectCase<>(SelectCase.this, boxMapper.apply(value), allowed(boxMapper));
        }

        public <U> O mapThenBack(Function<? super V, ? extends U> boxMapper) {
            map(boxMapper);
            return uplift(boxed, true);
        }

        public SelectCase<O, V> throwUnchecked(Supplier<? extends RuntimeException> eSupplier) {
            return chainWhen(allowed(eSupplier), () -> { throw eSupplier.get(); });
        }

        public SelectCase<O, V> throwUnchecked(RuntimeException e) {
            return chainWhen(allowed(e), () -> { throw e; });
        }

        public SelectCase<O, V> throwArgException() {
            return throwUnchecked(new IllegalArgumentException("Unsatisfied value [" + value + "] passing through select-case"));
        }
    }

    public Object unbox() {
        return boxed;
    }

    public Optional<?> optional() {
        return Optional.ofNullable(boxed);
    }

}
