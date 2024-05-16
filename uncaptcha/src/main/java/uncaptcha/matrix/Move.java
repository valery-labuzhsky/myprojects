package uncaptcha.matrix;

public class Move implements Transmutation {
    public final int x0;
    public final int y0;

    public Move(int x0, int y0) {
        this.x0 = x0;
        this.y0 = y0;
    }

    @Override
    public int transmuteX(int x, int y, Matrix matrix) {
        return x0 + x;
    }

    @Override
    public int transmuteY(int x, int y, Matrix matrix) {
        return y0 + y;
    }

}
