package uncaptcha.matrix;

import java.util.Arrays;

import static uncaptcha.Uncaptcha.WHITE;

public class FullMatrix extends Matrix {
    public int[] colors;
    int width;

    public FullMatrix(int[] colors, int width) {
        this.colors = colors;
        this.width = width;
    }

    @Override
    public int get(int x, int y) {
        int i = getI(x, y);
        if (i<0) return WHITE;
        return colors[i];
    }

    @Override
    public void set(int x, int y, int c) {
        colors[getI(x, y)] = c;
    }

    @Override
    public int get(int i) {
        return colors[i];
    }

    @Override
    public void set(int i, int color) {
        colors[i] = color;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return colors.length / width;
    }

    @Override
    public void fill(int color) {
        Arrays.fill(colors, color);
    }
}
