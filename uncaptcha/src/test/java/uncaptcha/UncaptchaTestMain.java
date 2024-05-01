package uncaptcha;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class UncaptchaTestMain {
    @Test
    public void testMain() throws IOException {
        Uncaptcha.main(new String[]{"src/test/resources/ocrfail--20240429_200349.png"});
    }

}