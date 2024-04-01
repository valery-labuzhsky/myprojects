package uncaptcha.matrix;

import static uncaptcha.Uncaptcha.BLACK;
import static uncaptcha.Uncaptcha.WHITE;

public abstract class Matrix {

    public abstract int get(int x, int y);

    public abstract void set(int x, int y, int c);

    public abstract int getWidth();

    public abstract int getHeight();

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
                    if (get(x, y) == WHITE) return false;
                }
                case '.' -> {
                    if (get(x, y) == BLACK) return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int y=0; y<getHeight(); y++) {
            for (int x=0; x<getWidth(); x++) {
                string.append(get(x, y)==BLACK?"x ":". ");
            }
            string.append('\n');
        }
        return string.toString();
    }
}
