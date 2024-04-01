package uncaptcha.matrix;

public class Fragment extends Transmuted {
    private final int x0;
    private final int y0;
    private final int width;
    private final int height;

    public Fragment(Matrix base, int x0, int y0, int width, int height) {
        super(base);
        this.x0 = x0;
        this.y0 = y0;
        this.width = width;
        this.height = height;
    }

    @Override
    protected int transmuteX(int x, int y) {
        return x0 + x;
    }

    @Override
    protected int transmuteY(int x, int y) {
        return y0 + y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
