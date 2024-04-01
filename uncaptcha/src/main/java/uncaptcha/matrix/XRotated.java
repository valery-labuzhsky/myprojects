package uncaptcha.matrix;

public class XRotated extends Transmuted {
    public XRotated(Matrix base) {
        super(base);
    }

    @Override
    protected int transmuteX(int x, int y) {
        return -x;
    }
}
