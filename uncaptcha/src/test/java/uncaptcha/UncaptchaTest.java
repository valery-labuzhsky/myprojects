package uncaptcha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UncaptchaTest {
    @Test
    public void test() throws IOException {
//        check("149881");
        check("283852");
        check("483821");
        check("564549");
        check("849104");
        check("386917");
        check("810864");
        check("458958");
        check("691551");
    }

    private static void check(String number) throws IOException {
        Assertions.assertEquals(Uncaptcha.detect("images/" + number + ".png"), number);
    }
}
