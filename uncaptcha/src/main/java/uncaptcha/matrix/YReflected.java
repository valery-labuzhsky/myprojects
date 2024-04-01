package uncaptcha.matrix;

public class YReflected extends Transmuted {
    public YReflected(Matrix base) {
        super(base);
    }

    @Override
    protected int transmuteY(int x, int y) {
        return getHeight() - y - 1;
    }
}
