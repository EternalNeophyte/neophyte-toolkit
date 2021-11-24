import fluent.Fluent;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    }
}
