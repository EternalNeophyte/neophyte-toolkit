import fluent.Fluent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Created on 22.11.2021 by
 *
 * @author alexandrov
 */
public class FluentsTest {

    @Test
    public void test() {
        String s = "Four";
        Fluent.loop(i -> i < s.length(), Integer::reverse);
    }

    @Test
    public void askingPassesWhenYes() {
        String s = Fluent.<String>ask(5 < 7)
                .yes( "Ok")
                .no("Wrong");
        assertEquals("Ok", s);
    }

    @Test
    public void askingPassesWhenYesNested() {
        Integer i = Fluent.<Integer>
                        ask(0 < 2)
                            .yesThenAsk(0 > 3)
                                .yes(10)
                                .noThenBack(-10)
                            .no(1);
        assertEquals(Integer.valueOf(-10) , i);
        Integer i2 = Fluent.<Integer>
                        ask(0 > 2)
                .yesThenAsk(0 > 3)
                .yes(10)
                .noThenBack(-10)
                .no(1);
        assertEquals(Integer.valueOf(1), i2);
        Integer i3 = Fluent.<Integer>
                        ask(0 < 2)
                .yesThenAsk(0 < 3)
                .yes(10)
                .noThenBack(-10)
                .no(1);
        assertEquals(Integer.valueOf(10), i3);
        Integer i4 = Fluent.<Integer>ask(true).yesThenAsk(true).yesThenBack(4).no(5);
        assertEquals(Integer.valueOf(4), i4);
        Integer i5 = Fluent.<Integer>ask(true).yesThenAsk(true).yesThenYield(6);
        assertEquals(Integer.valueOf(6), i5);
        Integer i6 = Fluent.<Integer>ask(false).yesThenYield(6);
        assertNull(i6);
    }

    @Test
    public void testTernaryOpOptional() {
        String result = Fluent.<String>
                ask(true)
                .yesThenAsk(false)
                    .yes("yes2")
                    .noThenBack(() -> {
                        System.out.println("Break!");
                        return "no2";
                    })
                .noThenOptional("no")
                .orElseThrow();
        assertEquals("no2", result);
    }

    @Test
    public void testConcurrentLoop() {
        Runnable task = () -> Fluent.loop(30, i -> System.out.println("#" + i));
        ForkJoinPool.commonPool().execute(ForkJoinTask.adapt(task));
    }

    @Test
    public void testSelectRange() {
        //Works fine with all numbers
        Fluent.select(100)
                .when(100)
                    .pass(i -> System.out.println("Blocked at 100"))
                .when(100)
                    .mapThenSelect(i -> "anv")
                    .whenRange("abc", "xez")
                    .passThenBack(s -> System.out.println("In str range"))
                .whenRange(1, 901)
                    .map(i -> "ayyy")
                .when(5)
                    .pass(i -> System.out.println("After pass"))
                .when(100)
                    .block(i -> System.out.println("After block"))
                .whenOther()
                    .pass(i -> System.out.println("Passed other"))
                .optional()
                .ifPresent(System.out::println);
    }

    @Test
    public void testNewSelectCase() {
        Fluent.select(5)
                .when(6)
                .throwArgException();
    }

    @Test
    public void testWhenThen() {
        var s = Fluent.select(2)
                .when(1, 2, 3)
                    .pass(v -> System.out.println("1-3"))
                .when(v -> v > 6)
                    .pass(System.out::println)
                .when(6)
                    .block(System.out::println)
                .when(9, 11)
                    .mapThenSelect(String::valueOf)
                    .when("3")
                        .box("ty")
                    .when("2")
                        .boxThenBack("saved")
                /*.optionalBox()
                .orElseThrow()*/;
        s.toString();
    }
}
