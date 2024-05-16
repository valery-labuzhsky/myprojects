package uncaptcha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uncaptcha.matrix.Matrix;
import uncaptcha.matrix.Transmutation;

public class MatrixTest {
    @Test
    public void testMatcher() {
        Matrix matrix = Matrix.create("""
                x x
                  \s
                """);
        Assertions.assertFalse(matrix.matches(" "));
    }

    @Test
    public void testRotate() {
        System.out.println("-");
        Matrix matrix = Matrix.create("""
                x \s
                  \s
                """);
        System.out.println(Transmutation.TRANSPOSE.mute(matrix));
    }
}
