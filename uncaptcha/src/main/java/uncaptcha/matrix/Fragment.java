package uncaptcha.matrix;

public class Fragment extends Transmuted {
    private final int width;
    private final int height;

    public Fragment(Matrix base, int x0, int y0, int width, int height) {
        super(base, new Move(x0, y0));
        this.width = width;
        this.height = height;
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
