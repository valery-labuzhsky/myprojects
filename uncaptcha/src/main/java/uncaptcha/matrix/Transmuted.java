package uncaptcha.matrix;

public class Transmuted extends Matrix {
    Matrix base;
    private final Transmutation mute;

    public Transmuted(Matrix base, Transmutation mute) {
        this.base = base;
        this.mute = mute;
    }

    @Override
    public int get(int x, int y) {
        return base.get(mute.transmuteX(x, y, base), mute.transmuteY(x, y, base));
    }

    @Override
    public void set(int x, int y, int c) {
        base.set(mute.transmuteX(x, y, base), mute.transmuteY(x, y, base), c);
    }

    @Override
    public int getWidth() {
        return Math.abs(mute.transmuteX(base.getWidth(), base.getHeight(), base) - mute.transmuteX(0, 0, base));
    }

    @Override
    public int getHeight() {
        return Math.abs(mute.transmuteY(base.getWidth(), base.getHeight(), base) - mute.transmuteY(0, 0, base));
    }
}
