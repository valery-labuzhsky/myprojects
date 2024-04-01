package uncaptcha.matrix;

public class Transposed extends Transmuted {

    public Transposed(Matrix base) {
        super(base);
    }

    @Override
    protected int transmuteX(int x, int y) {
        return y;
    }

    @Override
    protected int transmuteY(int x, int y) {
        return x;
    }

    @Override
    public int getWidth() {
        return super.getHeight();
    }

    @Override
    public int getHeight() {
        return super.getWidth();
    }
}
