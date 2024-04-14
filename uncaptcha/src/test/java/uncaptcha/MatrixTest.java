package uncaptcha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uncaptcha.matrix.Matrix;

public class MatrixTest {
    @Test
    public void testMatcher() {
        Matrix matrix = Matrix.create("""
                x x
                  \s
                """);
        Assertions.assertFalse(matrix.matches(" "));
    }
}
