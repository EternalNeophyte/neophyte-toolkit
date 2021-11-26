package fluent;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public class Fluent {

    public static void loop(int bound, IntConsumer indexConsumer) {
        for(int i = 0; i < bound; i++) {
            indexConsumer.accept(i);
        }
    }

    public static void loop(IntSupplier bound, IntConsumer indexConsumer) {
        loop(bound.getAsInt(), indexConsumer);
    }

    public static void loop(IntPredicate indexPredicate, IntConsumer indexConsumer) {
        int index = 0;
        while (indexPredicate.test(index)) {
            indexConsumer.accept(index++);
        }
    }

    public static void loop(boolean condition, IntConsumer indexConsumer) {
        loop(i -> condition, indexConsumer);
    }

    public static void loop(BooleanSupplier condition, IntConsumer indexConsumer) {
        loop(i -> condition.getAsBoolean(), indexConsumer);
    }

    public static <T> TernaryOp<T> ask(boolean condition) {
        return new TernaryOp<>(condition);
    }

    public static <T> TernaryOp<T> ask(BooleanSupplier condition) {
        return new TernaryOp<>(condition.getAsBoolean());
    }

    public static SelectCase select(Object arg) {
        //ToDo specify
        return null;
    }

}
