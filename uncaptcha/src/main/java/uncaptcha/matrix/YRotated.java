package uncaptcha.matrix;

public class YRotated extends Transmuted {
    public YRotated(Matrix base) {
        super(base);
    }

    @Override
    protected int transmuteY(int x, int y) {
        return -y;
    }
}
