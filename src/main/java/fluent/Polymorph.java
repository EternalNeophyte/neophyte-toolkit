package fluent;

import support.Chaining;

import java.util.function.Function;

/**
 *
 * @param <O> origin type
 * @param <T> stands for 'this'
 * @param <V> type of value stored in
 */
public class Polymorph<O, T extends Polymorph<O, T, V>, V> implements Chaining<T> {

    boolean actionAllowed;
    O origin;
    V value;

    public Polymorph(boolean actionAllowed, O origin, V value) {
        this.actionAllowed = actionAllowed;
        this.origin = origin;
        this.value = value;
    }

    //ToDo etc унаследовать Cascade от этого класса, но expander оставить в нем
    //добавить сюда map W - новый тип значения

    /**
     *
     * @param valueMapper
     * @param <E> extension
     * @param <U> type of mapped value
     * @return
     */
    public <E extends Polymorph<T, E, U>, U> E morph(Function<? super V, ? extends U> valueMapper) {
        return (E) new Polymorph<T, E, U>(actionAllowed, (T) this, valueMapper.apply(value));
    }




}
