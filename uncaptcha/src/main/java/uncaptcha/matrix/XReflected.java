package uncaptcha.matrix;

public class XReflected extends Transmuted {
    public XReflected(Matrix base) {
        super(base);
    }

    @Override
    protected int transmuteX(int x, int y) {
        return getWidth() - x - 1;
    }

}
