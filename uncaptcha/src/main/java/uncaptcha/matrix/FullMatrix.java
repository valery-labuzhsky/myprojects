package uncaptcha.matrix;

public class FullMatrix extends Matrix {
    int[] colors;
    int width;

    public FullMatrix(int[] colors, int width) {
        this.colors = colors;
        this.width = width;
    }

    @Override
    public int get(int x, int y) {
        return colors[y * width + x];
    }

    @Override
    public void set(int x, int y, int c) {
        colors[y * width + x] = c;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return colors.length / width;
    }
}
