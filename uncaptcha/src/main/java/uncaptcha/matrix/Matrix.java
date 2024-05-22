package uncaptcha.matrix;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static uncaptcha.Uncaptcha.*;

public abstract class Matrix {

    public boolean findMatchingSymmetry(String pattern, Symmetry symmetry, Consumer<Symmetry> action) {
        if (matches(pattern)) {
            action.accept(Symmetry.NONE);
            return true;
        } else if (symmetry.t.mute(this).matches(pattern)) {
            action.accept(symmetry);
            return true;
        }
        return false;
    }

    public boolean findMatchingRotation(String pattern, Consumer<Rotation> action) {
        for (Rotation r : Rotation.values()) {
            if (r.t.mute(this).matches(pattern)) {
                action.accept(r);
                return true;
            }
        }
        return false;
    }

    public void forAllXY(BiConsumer<Integer, Integer> action) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                action.accept(x, y);
            }
        }
    }

    public abstract int get(int x, int y);

    public abstract void set(int x, int y, int c);

    public IntStream getPerimeter() {
        return Stream.of(IntStream.range(0, getWidth() - 1).map(x -> getI(x, 0)),
                        IntStream.range(0, getHeight() - 1).map(y -> getI(2, y)),
                        IntStream.range(0, getWidth() - 1).map(x -> getI(2 - x, 2)),
                        IntStream.range(0, getHeight() - 1).map(y -> getI(0, 2 - y)))
                .flatMapToInt(s -> s);
    }

    public int get(int i) {
        return get(getX(i), getY(i));
    }

    public void set(int i, int color) {
        set(getX(i), getY(i), color);
    }

    public boolean isBridge() {
        int[] perimeter = getPerimeter().toArray();
        int j = 1;
        boolean black = get(perimeter[0]) == BLACK;
        int borders = 0;
        while (j < perimeter.length) {
            if (black) {
                if (get(perimeter[j]) == WHITE) {
                    black = false;
                    borders++;
                    j += 1;
                } else {
                    if (j % 2 == 0) {
                        j += 1;
                    } else {
                        j += 2;
                    }
                }
            } else {
                if (get(perimeter[j]) == BLACK) {
                    black = true;
                    borders++;
                }
                j += 1;
            }
        }
        return borders > 2;
    }

    public IntStream getPoints() {
        return IntStream.range(0, getWidth()).flatMap(x -> IntStream.range(0, getHeight()).map(y -> getI(x, y)));
    }

    public int count() {
        return getPoints().map(i -> get(i) == BLACK ? 1 : 0).sum();
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public static Matrix create(int width, int height) {
        return new FullMatrix(new int[width * height], width);
    }

    public static Matrix create(String template) {
        String[] lines = template.split("\n");
        int width = Arrays.stream(lines).mapToInt(line -> (line.length() + 1) / 2).max().orElseThrow();
        FullMatrix matrix = new FullMatrix(new int[lines.length * width], width);
        matrix.set(template);
        return matrix;
    }

    public void set(String template) {
        String[] lines = template.split("\n");
        for (int y = 0; y < lines.length; y++) {
            int w = (lines[y].length() + 1) / 2;
            for (int x = 0; x < w; x ++) {
                switch (lines[y].charAt(x * 2)) {
                    case 'x' -> set(x, y, BLACK);
                    case ' ' -> set(x, y, WHITE);
                    default -> set(x, y, GRAY);
                }
            }
            for (int x = w; x < getWidth(); x ++) {
                set(x, y, WHITE);
            }
        }
    }

    public boolean matches(String template) {
        String[] lines = template.split("\n");
        for (int y = 0; y < lines.length; y++) {
            if (!matches(lines[y], y)) return false;
        }
        return true;
    }

    public boolean matchesAny(String template) {
        String[] lines = template.split("\n");
        String[][] templines = new String[lines.length][];
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            String[] templine = line.split("\\|");
            templines[y] = templine;
        }
        template:
        for (int t = 0; t < templines[0].length; t++) {
            for (int y = 0; y < templines.length; y++) {
                if (!matches(templines[y][t], y)) continue template;
            }
            return true;
        }
        return false;
    }

    public boolean matches(String line, int y) {
        for (int x = 0; x < (line.length() + 1) / 2; x += 1) {
            switch (line.charAt(x * 2)) {
                case 'x' -> {
                    if (get(x, y) != BLACK) return false;
                }
                case ' ' -> {
                    if (get(x, y) != WHITE) return false;
                }
                case '+' -> {
                    if (get(x, y) == WHITE) return false;
                }
                case '-' -> {
                    if (get(x, y) == BLACK) return false;
                }
                case '*' -> {
                    if (get(x, y) != GRAY) return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (get(x, y) == BLACK) {
                    string.append("x ");
                } else if (get(x, y) == WHITE) {
                    string.append("  ");
                } else {
                    string.append(". ");
                }
            }
            string.append('\n');
        }
        return string.toString();
    }

    public int getX(int i) {
        return i % getWidth();
    }

    public int getY(int i) {
        return i / getWidth();
    }

    public int getI(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return -1;
        return y * getWidth() + x;
    }

    public Matrix fill(int color) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                set(x, y, color);
            }
        }
        return this;
    }
}
