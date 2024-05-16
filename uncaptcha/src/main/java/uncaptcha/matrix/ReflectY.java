package uncaptcha.matrix;

public class ReflectY implements Transmutation {

    @Override
    public int transmuteY(int x, int y, Matrix matrix) {
        return matrix.getHeight() - y - 1;
    }
}
