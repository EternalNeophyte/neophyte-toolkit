package fluent;

import support.Chaining;

import java.util.function.Function;

/**
 * Created on 01.12.2021 by
 *
 * @author alexandrov
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
    public <U extends Polymorph<T, U, W>, W> U morph(Function<? super V, ? extends W> valueMapper) {
        return (U) new Polymorph<T, U, W>(actionAllowed, (T) this, valueMapper.apply(value));
    }
}
