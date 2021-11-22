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
        String s = Fluent.<String>asking(5 < 7)
                .yes( "Ok")
                .no("Wrong");
        assertEquals("Ok", s);
    }

    public void askingPassesWhenYesNested() {
        Integer i = Fluent.<Integer>asking(0 < 2)
                .yesThenAsk(0 > 3)
                .no(1);
    }
}
