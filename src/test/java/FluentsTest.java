import fluent.Fluent;
import org.junit.jupiter.api.Assertions;
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
}
