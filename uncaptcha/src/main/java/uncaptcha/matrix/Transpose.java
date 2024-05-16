package uncaptcha.matrix;

public class Transpose implements Transmutation {
    @Override
    public int transmuteX(int x, int y, Matrix matrix) {
        return y;
    }

    @Override
    public int transmuteY(int x, int y, Matrix matrix) {
        return x;
    }
}
