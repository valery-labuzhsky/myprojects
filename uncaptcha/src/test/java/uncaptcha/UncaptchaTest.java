package uncaptcha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UncaptchaTest {
    @Test
    public void test() throws IOException {
        check("386917");
    }

    private static void check(String number) throws IOException {
        Assertions.assertEquals(Uncaptcha.detect("images/" + number + ".png"), number);
    }
}
