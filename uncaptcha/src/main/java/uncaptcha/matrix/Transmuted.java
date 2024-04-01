package uncaptcha.matrix;

public abstract class Transmuted extends Matrix {
    Matrix base;

    public Transmuted(Matrix base) {
        this.base = base;
    }

    @Override
    public int get(int x, int y) {
        return base.get(transmuteX(x, y), transmuteY(x, y));
    }

    @Override
    public void set(int x, int y, int c) {
        base.set(transmuteX(x, y), transmuteY(x, y), c);
    }

    protected int transmuteX(int x, int y) {
        return x;
    }
    protected int transmuteY(int x, int y) {
        return y;
    }

    @Override
    public int getWidth() {
        return base.getWidth();
    }

    @Override
    public int getHeight() {
        return base.getHeight();
    }
}
