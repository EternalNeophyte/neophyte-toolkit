import fluent.Fluent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

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
                                .noThenBreak(-10)
                            .no(1);
        assertEquals(Integer.valueOf(-10) , i);
        Integer i2 = Fluent.<Integer>
                        ask(0 > 2)
                .yesThenAsk(0 > 3)
                .yes(10)
                .noThenBreak(-10)
                .no(1);
        assertEquals(Integer.valueOf(1), i2);
        Integer i3 = Fluent.<Integer>
                        ask(0 < 2)
                .yesThenAsk(0 < 3)
                .yes(10)
                .noThenBreak(-10)
                .no(1);
        assertEquals(Integer.valueOf(10), i3);
        Integer i4 = Fluent.<Integer>ask(true).yesThenAsk(true).yesThenBreak(4).no(5);
        assertEquals(Integer.valueOf(4), i4);
        Integer i5 = Fluent.<Integer>ask(true).yesThenAsk(true).yesThenYield(6);
        assertEquals(Integer.valueOf(6), i5);
        Integer i6 = Fluent.<Integer>ask(false).yesThenYield(6);
        assertNull(i6);
    }
}
