package uncaptcha.matrix;

public class ReflectX implements Transmutation {

    @Override
    public int transmuteX(int x, int y, Matrix matrix) {
        return matrix.getWidth() - x - 1;
    }
}
