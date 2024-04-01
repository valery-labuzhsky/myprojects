package uncaptcha;

import uncaptcha.matrix.Fragment;
import uncaptcha.matrix.FullMatrix;
import uncaptcha.matrix.Matrix;
import uncaptcha.matrix.Transposed;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Uncaptcha {

    public static final int WHITE = rgb(0xff);
    public static final int BLACK = rgb(0);

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File(args[0]));
        image = transformOld(image, Integer.parseInt(args[2]), Double.parseDouble(args[3]));
        ImageIO.write(image, "png", new File(args[1]));
    }

    public static BufferedImage transformOld(BufferedImage image, int x0, double c) {
        BufferedImage orig = image;
        image = new Contrast().apply(image);
        image = new RemoveHoles().apply(image);
        image = new Count().apply(image);
        Frame frame = new Frame();
        image = frame.apply(image);
        image = new Cut().apply(image);
        FineFrame fineFrame = new FineFrame();
        image = fineFrame.apply(image);

        orig = frame.rotation().combine(fineFrame).apply(orig);
        orig = new Cut().apply(orig);
        orig = new Stretch(x0, c).apply(orig);
        return orig;
    }

    public static BufferedImage transform(BufferedImage image) {
        image = new Contrast().apply(image);
        image = new RemoveHoles().apply(image);
        BufferedImage orig = image;
        image = new Count().apply(image);
        Frame frame = new Frame();
        image = frame.apply(image);
        image = new Cut().apply(image);
        FineFrame fineFrame = new FineFrame();
        image = fineFrame.apply(image);

        orig = frame.rotation().combine(fineFrame).apply(orig);
        orig = new Cut().apply(orig);
        orig = new Window().apply(orig);
        orig = new Split().apply(orig);
        orig = new Shrink1().apply(orig);
        orig = new Shrink2().apply(orig);
        orig = new Compact().apply(orig);
        return orig;
    }

    public static String detect(String path) throws IOException {
        return detect(ImageIO.read(new File(path)));
    }

    public static String detect(BufferedImage image) {
        image = new Contrast().apply(image);
        image = new RemoveHoles().apply(image);
        BufferedImage orig = image;
        image = new Count().apply(image);
        Frame frame = new Frame();
        image = frame.apply(image);
        image = new Cut().apply(image);
        FineFrame fineFrame = new FineFrame();
        image = fineFrame.apply(image);

        orig = frame.rotation().combine(fineFrame).apply(orig);
        orig = new Cut().apply(orig);
        orig = new Window().apply(orig);
        orig = new Split().apply(orig);
        orig = new Shrink1().apply(orig);
        orig = new Shrink2().apply(orig);
        Compact compact = new Compact();
        orig = compact.apply(orig);
        return compact.numbers.toString();
    }

    public static class Stretch extends Filter {
        int x0;
        double c;

        public Stretch(int x0, double c) {
            this.x0 = x0;
            this.c = c;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            int x02 = (int) (x0 * c);
            double c2 = (width - x0 * c) / (width - x0);
            for (int x = x02; x < width; x++) {
                for (int y = 0; y < out.length / width; y++) {
                    out[y * width + x] = in[(int) (y * width + x0 + (x - x02) / c2)];
                }
            }

            for (int x = 0; x < x02; x++) {
                for (int y = 0; y < out.length / width; y++) {
                    out[y * width + x] = in[(int) (y * width + x / c)];
                }
            }
        }
    }

    public static class Compact extends Filter {
        StringBuilder numbers = new StringBuilder();


        @Override
        protected void process(int[] in, int[] out, int width) {
            int sx = 0;
            int sy = 0;
            int ex = -1;
            int ey = 0;

            System.arraycopy(in, 0, out, 0, in.length);

            while (true) {
                boolean found = false;

                found:
                for (int x = ex + 1; x < width; x++) {
                    for (int y = 0; y < in.length / width; y++) {
                        if (bool(in, width, x, y) == 0) {
                            sx = x;
                            sy = y;
                            found = true;
                            break found;
                        }
                    }
                }

                if (!found) break;

                ex = sx;
                ey = sy;

                while (found) {
                    found = false;
                    for (int x = sx; x <= ex + 1; x++) {
                        if (bool(in, width, x, sy - 1) == 0) {
                            found = true;
                            sy--;
                            break;
                        }
                    }
                    for (int x = sx; x <= ex + 1; x++) {
                        if (bool(in, width, x, ey + 1) == 0) {
                            found = true;
                            ey++;
                            break;
                        }
                    }
                    for (int y = sy; y <= ey + 1; y++) {
                        if (bool(in, width, sx - 1, y) == 0) {
                            found = true;
                            sx--;
                            break;
                        }
                    }
                    for (int y = sy; y <= ey + 1; y++) {
                        if (bool(in, width, ex + 1, y) == 0) {
                            found = true;
                            ex++;
                            break;
                        }
                    }
                }

                FullMatrix all = new FullMatrix(out, width);

                int ch = ey - sy + 1;
                int cw = ex - sx + 1;
                if (cw < ch * 3 / 5) cw = ch * 3 / 5;

                Fragment cipher = new Fragment(all, ex - cw + 1, sy, cw, ch);

                compactToHeight(cipher, 5);
                compactToHeight(new Transposed(cipher), 3);

                Fragment number = new Fragment(all, ex - 3 + 1, ey - 5 + 1, 3, 5);
                if (number.matches("""
                        x x x
                        x . x
                        x x x
                        x . x
                        x x x
                        """)) {
                    this.numbers.append('8');
                } else if (number.matches("""
                        x x x
                        x . x
                        x x x
                        . . x
                        x x x
                        """)) {
                    this.numbers.append('9');
                } else if (number.matches("""
                        x x x
                        x . .
                        x x x
                        x . x
                        x x x
                        """)) {
                    this.numbers.append('6');
                } else if (number.matches("""
                        x x x
                        x . x
                        x . x
                        x . x
                        x x x
                        """)) {
                    this.numbers.append('0');
                } else if (number.matches("""
                        x x x
                        x . .
                        x x x
                        . . x
                        x x x
                        """)) {
                    this.numbers.append('5');
                } else if (number.matches("""
                        x x x
                        . . x
                        x x x
                        x . .
                        x x x
                        """)) {
                    this.numbers.append('2');
                } else if (number.matches("""
                        x x x
                        . . x
                          x x
                        . . x
                        x x x
                        """)) {
                    this.numbers.append('3');
                } else if (number.matches("""
                        x x x
                        . . x
                        . x .
                        . x .
                          x\s\s
                        """)) {
                    this.numbers.append('7');
                } else if (number.matches("""
                        . x x
                        . . x
                        . . x
                        . . x
                        . . x
                        """)) {
                    this.numbers.append('1');
                } else if (number.matches("""
                          x x
                        x . x
                        x x x
                        . . x
                        . . x
                        """)) {
                    this.numbers.append('4');
                } else {
                    this.numbers.append('?');
                }
            }

            System.out.println(numbers);
        }

        private void compactToHeight(Matrix cipher, int target) {
            int height = cipher.getHeight();
            int toCompact = height - target;
            double yc = 0;
            while (toCompact > 1) {
                int y = (int) yc;

                if (canCompact(cipher, y)) {
                    compactLine(cipher, y);
                    yc += (height - 2d) / (toCompact - 1);
                    height--;
                    toCompact--;
                } else {
                    yc += 1;
                }
            }

            if (toCompact == 1) {
                for (int y = cipher.getHeight() - 2; y >= 0; y--) {
                    if (canCompact(cipher, y)) {
                        compactLine(cipher, y);
                        return;
                    }
                }
            }
        }

        private static boolean canCompact(Matrix cipher, int y) {
            for (int x = 0; x < cipher.getWidth(); x++) {
                String naughties = """
                          x  |  x  |  x x|x x  |
                        x . x|. . .|x . .|. . x|
                          x  |  x  |  x x|x x  |
                        """;
                if (new Fragment(cipher, x - 1, y, 3, 3).matchesAny(naughties)) {
                    return false;
                }
                if (new Fragment(cipher, x - 1, y - 1, 3, 3).matchesAny(naughties)) {
                    return false;
                }
            }
            return true;
        }

        private static void compactLine(Matrix cipher, int y) {
            for (int x = 0; x < cipher.getWidth(); x++) {
                if (cipher.get(x, y) == BLACK) {
                    cipher.set(x, y + 1, BLACK);
                }
            }

            for (int yo = y; yo >= 0; yo--) {
                for (int x = 0; x < cipher.getWidth(); x++) {
                    cipher.set(x, yo, cipher.get(x, yo - 1));
                }
            }
        }
    }

    public static class Shrink2 extends Filter {
        @Override
        public int scale(int x) {
            return x / 2;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            int sw = scale(width);
            int sh = scale(in.length / width);

            int[] square = new int[4];
            for (int x = 0; x < sw; x++) {
                for (int y = 0; y < sh; y++) {
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dy = 0; dy < 2; dy++) {
                            square[dy * 2 + dx] = bool(in[(y * 2 + dy) * width + x * 2 + dx]);
                        }
                    }
                    if (Arrays.stream(square).sum() <= 1) {
                        out[y * sw + x] = BLACK;
                    } else if (square[0] + square[3] == 0 || square[1] + square[2] == 0) {
                        out[y * sw + x] = BLACK;
                    } else {
                        out[y * sw + x] = WHITE;
                    }
                }
            }

            for (int x = 0; x < sw - 1; x++) {
                for (int y = 1; y < sh - 1; y++) {
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dy = 0; dy < 2; dy++) {
                            square[dy * 2 + dx] = bool(in[(y * 2 + dy) * width + x * 2 + dx + 1]);
                        }
                    }
                    if (Arrays.stream(square).sum() == 0) {
                        if (out[y * sw + x] == WHITE && out[y * sw + x + 1] == WHITE) {
                            int left = bool(out, sw, x, y - 1) + bool(out, sw, x, y + 1);
                            int right = bool(out, sw, x + 1, y - 1) + bool(out, sw, x + 1, y + 1);
                            if (left <= right) {
                                out[y * sw + x] = BLACK;
                            } else {
                                out[y * sw + x + 1] = BLACK;
                            }
                        }
                    } else if (square[0] + square[2] == 0) {
                        out[y * sw + x] = BLACK;
                    } else if (square[1] + square[3] == 0) {
                        out[y * sw + x + 1] = BLACK;
                    }
                }
            }

            for (int y = 0; y < sh - 1; y++) {
                for (int x = 1; x < sw - 1; x++) {
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dy = 0; dy < 2; dy++) {
                            square[dy * 2 + dx] = bool(in[(y * 2 + dy + 1) * width + x * 2 + dx]);
                        }
                    }
                    if (Arrays.stream(square).sum() == 0) {
                        if (out[y * sw + x] == WHITE && out[(y + 1) * sw + x] == WHITE) {
                            int left = bool(out, sw, x - 1, y) + bool(out, sw, x + 1, y);
                            int right = bool(out, sw, x - 1, y + 1) + bool(out, sw, x + 1, y + 1);
                            if (left <= right) {
                                out[y * sw + x] = BLACK;
                            } else {
                                out[(y + 1) * sw + x] = BLACK;
                            }
                        }
                    } else if (square[0] + square[1] == 0) {
                        out[y * sw + x] = BLACK;
                    } else if (square[2] + square[3] == 0) {
                        out[(y + 1) * sw + x] = BLACK;
                    }
                }
            }
        }
    }

    public static class Shrink1 extends Filter {
        @Override
        public int scale(int x) {
            return x / 2;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            int sw = scale(width);
            int sh = scale(in.length / width);

            int[] square = new int[4];
            for (int x = 0; x < sw; x++) {
                for (int y = 0; y < sh; y++) {
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dy = 0; dy < 2; dy++) {
                            square[dy * 2 + dx] = bool(in[(y * 2 + dy) * width + x * 2 + dx]);
                        }
                    }
                    if (Arrays.stream(square).sum() <= 1) {
                        out[y * sw + x] = BLACK;
                    } else {
                        out[y * sw + x] = WHITE;
                    }
                }
            }
        }
    }

    public static class Split extends Filter {

        @Override
        protected void process(int[] in, int[] out, int width) {
            int[] dxs = new int[]{6, 24, 45, 72, 105, 136, 172};
            int height = in.length / width;
            int bestX = 0;
            int bestY = 0;
            for (int x = -10; x < 10; x++) {
                ArrayList<Integer> ys = new ArrayList<>();
                for (int i = 1; i < dxs.length - 1; i++) {
                    int dx = dxs[i];
                    int fx = x;
                    ys.add(IntStream.range(10, 30)
                            .filter(y -> bool(in, width, fx + dx, y) == 0)
                            .findFirst().orElse(30));
                    ys.add(IntStream.range(10, 30)
                            .filter(y -> bool(in, width, fx + dx, height - y - 1) == 0)
                            .findFirst().orElse(30));
                }
                Collections.sort(ys);
                int y = ys.get(ys.size() / 2);
                if (y > bestY) {
                    bestY = y;
                    bestX = x;
                }
            }
            Arrays.fill(out, WHITE);
            double[] ks = new double[]{2, 1.5, 1, 0.8, 0.8, 0.8};
            for (int n = 0; n < 6; n++) {
                int sx = (dxs[n + 1] + dxs[n]) / 2 + bestX;
                int lw = 33;
                int tx = 1 + n * lw + lw / 2;
                double k = ks[n];

                if (lw > (dxs[n + 1] - dxs[n]) * k) lw = (int) ((dxs[n + 1] - dxs[n]) * k);
                for (int x = -lw / 2 + 1; x < lw / 2 + 1; x++) {
                    for (int y = 0; y < height; y++) {
                        int xx = (int) (x / k + sx);
                        if (xx >= 0 && xx < width) {
                            out[y * width + x + tx] = in[y * width + xx];
                        }
                    }
                }

//                for (int y = 0; y < height; y++) {
//                    out[y * width + 1 + n * 33] = BLACK;
//                }
            }
        }
    }

    public static class Window extends Filter {
        @Override
        protected void process(int[] in, int[] out, int width) {
            int l = 48;
            int height = in.length / width;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < (height - l) / 2; y++) {
                    out[y * width + x] = WHITE;
                    out[(height - y - 1) * width + x] = WHITE;
                }
                for (int y = (height - l) / 2; y < height - (height - l) / 2; y++) {
                    out[y * width + x] = in[y * width + x];
                }
            }
        }
    }

    public static class FineFrame extends Rotate {

        double rang(double ang) {
            if (ang > Math.PI / 2) return ang - Math.PI;
            if (ang < -Math.PI / 2) return ang + Math.PI;
            return ang;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            measure(in, width);
            super.process(in, out, width);
        }

        private void measure(int[] in, int width) {
            ArrayList<Integer> xs = new ArrayList<>();
            ArrayList<Integer> ys = new ArrayList<>();

            int high = 17;
            int height = in.length / width;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height - high; y++) {
                    int i = y * width + x;
                    if (in[i] == BLACK) {
                        xs.add(x);
                        ys.add(y + high);
                        break;
                    }
                }
                for (int y = height - 1; y >= high; y--) {
                    int i = y * width + x;
                    if (in[i] == BLACK) {
                        xs.add(x);
                        ys.add(y - high);
                        break;
                    }
                }
            }

            Collections.sort(xs);
            Collections.sort(ys);

            x0 = xs.get(xs.size() / 2);
            y0 = ys.get(ys.size() / 2);

            ArrayList<Double> angs = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height - high; y++) {
                    int i = y * width + x;
                    if (in[i] == BLACK) {
                        int x1 = (int) (x - x0);
                        y -= y0;
                        y += high;
                        if (Math.sqrt(x1 * x1 + y * y) > 40) {
                            angs.add(rang(Math.atan2(y, x1)));
                        }
                        break;
                    }
                }
                for (int y = height - 1; y >= high; y--) {
                    int i = y * width + x;
                    if (in[i] == BLACK) {
                        int x1 = (int) (x - x0);
                        y -= y0;
                        y -= high;
                        if (Math.sqrt(x1 * x1 + y * y) > 40) {
                            angs.add(rang(Math.atan2(y, x1)));
                        }
                        break;
                    }
                }
            }

            Collections.sort(angs);
            ang = angs.get(angs.size() / 2);

            int cx = width / 2;
            int cy = in.length / width / 2;
            x0 -= cx;
            y0 -= cy;
        }
    }

    public static class Cut extends Filter {
        @Override
        protected int scaleHeight(int height) {
            return height / 3;
        }

        @Override
        protected int scaleWidth(int width) {
            return width;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            for (int i = 0; i < out.length; i++) {
                out[i] = in[i + width * scaleHeight(width)];
            }
        }
    }

    public static class Rotate extends Filter {

        protected double x0;
        protected double y0;
        protected double ang;

        public Rotate rotation() {
            Rotate rotate = new Rotate();
            rotate.x0 = x0;
            rotate.y0 = y0;
            rotate.ang = ang;
            return rotate;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            int cx = width / 2;
            int cy = in.length / width / 2;

            double horz = -Math.tan(ang / 2);
            double vert = Math.sin(ang);
            for (int i = 0; i < out.length; i++) {
                int x = i % width - cx;
                int y = i / width - cy;

                x -= x0;
                y -= y0;

                x += horz * y;
                y += vert * x;
                x += horz * y;

                x += x0;
                y += y0;

                y += y0;

                y += cy;
                x += cx;

                if (x >= 0 && x < width && y >= 0 && y < out.length / width) {
                    out[i] = in[y * width + x];
                } else {
                    out[i] = WHITE;
                }
            }
        }

        public Rotate combine(Rotate rotate) {
            Rotate combine = rotation();
            combine.x0 += rotate.x0;
            combine.y0 += rotate.y0;
            combine.ang += rotate.ang;
            return combine;
        }
    }

    public static class Frame extends Rotate {

        double rang(double ang) {
            if (ang > Math.PI / 2) return ang - Math.PI;
            if (ang < -Math.PI / 2) return ang + Math.PI;
            return ang;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            measure(in, width);
            super.process(in, out, width);
        }

        private void measure(int[] in, int width) {
            int cx = width / 2;
            int cy = in.length / width / 2;

            ArrayList<Integer> xs = new ArrayList<>();
            ArrayList<Integer> ys = new ArrayList<>();

            for (int i = 0; i < in.length; i++) {
                int x = i % width - cx;
                int y = i / width - cy;

                if (bool(in[i]) == 0) {
                    xs.add(x);
                    ys.add(y);
                }
            }

            Collections.sort(xs);
            Collections.sort(ys);

            x0 = xs.get(xs.size() / 2);
            y0 = ys.get(ys.size() / 2);

            ArrayList<Double> angs = new ArrayList<>();
            for (int i = 0; i < in.length; i++) {
                int x = i % width - cx;
                int y = i / width - cy;

                x -= x0;
                y -= y0;

                if (bool(in[i]) == 0 && Math.sqrt(x * x + y * y) > 40) {
                    angs.add(rang(Math.atan2(y, x)));
                }
            }

            Collections.sort(angs);
            ang = angs.get(angs.size() / 2);
        }
    }

    public static class Count extends Filter {
        final static float SQR = (float) Math.sqrt(2);

        @Override
        protected void process(int[] in, int[] out, int width) {
            float[] lvls = new float[in.length];

            TreeMap<Float, HashSet<Integer>> next = new TreeMap<>();

            for (int i = 0; i < in.length; i++) {
                if (bool(in[i]) > 0) {
                    lvls[i] = 0;
                    next.computeIfAbsent(0f, l -> new HashSet<>()).add(i);
                } else {
                    lvls[i] = Float.MAX_VALUE;
                }
            }

            while (!next.isEmpty()) {
                Map.Entry<Float, HashSet<Integer>> entry = next.entrySet().iterator().next();
                float d = entry.getKey();
                HashSet<Integer> ix = entry.getValue();
                int i = ix.iterator().next();
                ix.remove(i);
                if (ix.isEmpty()) {
                    next.remove(d);
                }

                int y = i / width;
                int x = i % width;

                check(lvls, next, d, width, x + 1, y, 1f);
                check(lvls, next, d, width, x - 1, y, 1f);
                check(lvls, next, d, width, x, y + 1, 1f);
                check(lvls, next, d, width, x, y - 1, 1f);

                check(lvls, next, d, width, x + 1, y + 1, SQR);
                check(lvls, next, d, width, x - 1, y + 1, SQR);
                check(lvls, next, d, width, x + 1, y + 1, SQR);
                check(lvls, next, d, width, x + 1, y - 1, SQR);
            }

            for (int x = 1; x < width - 1; x++) {
                for (int y = 1; y < in.length / width - 1; y++) {
                    int i = y * width + x;
                    float lvl = lvls[i];
                    if (lvl > 1 && lvl < 5) {
                        boolean max = true;
                        ret:
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -width; dy <= width; dy += width) {
                                float l = lvls[i + dy + dx];
                                if (l > lvl) {
                                    max = false;
                                    break ret;
                                }
                            }
                        }
                        if (max) {
                            out[i] = BLACK;
                        } else {
                            out[i] = WHITE;
                        }
                    } else {
                        out[i] = WHITE;
                    }
                }
            }

            for (int x = 0; x < width; x++) {
                out[x] = WHITE;
                out[(width - 1) * width + x] = WHITE;
                out[x * width] = WHITE;
                out[x * width + width - 1] = WHITE;
            }
//            for (int i = 0; i < lvls.length; i++) {
//                float lvl = lvls[i];
//                if (lvl > 1 && lvl < 5) {
//                    out[i] = rgb(0);
//                } else {
//                    out[i] = rgb(255);
//                }
//            }
        }

        private static void check(float[] lvls, TreeMap<Float, HashSet<Integer>> next, float d, int width, int x, int y, float dd) {
            if (x < 0) return;
            if (y < 0) return;
            if (x >= width) return;
            if (y >= lvls.length / width) return;
            check(lvls, next, d, y * width + x, dd);
        }

        private static void check(float[] lvls, TreeMap<Float, HashSet<Integer>> next, float d, int j, float dd) {
            float lvl = lvls[j];
            float lvl2 = d + dd;
            if (lvl > lvl2) {
                lvls[j] = lvl2;
                next.computeIfAbsent(lvl2, l -> new HashSet<>()).add(j);
            }
        }

    }


    public static class RemoveHoles extends Filter {
        @Override
        protected void process(int[] in, int[] out, int width) {
            System.arraycopy(in, 0, out, 0, in.length);

            for (int x = 1; x < width - 1; x++) {
                for (int y = 1; y < in.length / width - 1; y++) {
                    int c = bool(in, width, x, y);
                    int uc = bool(in, width, x, y + 1);
                    int cr = bool(in, width, x + 1, y);
                    int dc = bool(in, width, x, y - 1);
                    int cl = bool(in, width, x - 1, y);
                    if (c > 0) {
                        if ((uc == 0 && dc == 0) &&
                                (cr == 0 && cl == 0)) {
                            out[y * width + x] = BLACK;
                        }
                    }
                }
            }

            for (int x = 1; x < width - 1; x++) {
                for (int y = 1; y < in.length / width - 1; y++) {
                    int c = bool(out, width, x, y);
                    int uc = bool(out, width, x, y + 1);
                    int cr = bool(out, width, x + 1, y);
                    int dc = bool(out, width, x, y - 1);
                    int cl = bool(out, width, x - 1, y);
                    if (c == 0) {
                        if ((uc > 0 && dc > 0) ||
                                (cr > 0 && cl > 0)) {
                            out[y * width + x] = WHITE;
                        }
                    }
                }
            }

            for (int x = 1; x < width - 1; x++) {
                for (int y = 1; y < in.length / width - 1; y++) {
                    int c = bool(out, width, x, y);
                    int uc = bool(out, width, x, y + 1);
                    int dc = bool(out, width, x, y - 1);
                    int cr = bool(out, width, x + 1, y);
                    int cl = bool(out, width, x - 1, y);
                    if (c > 0) {
                        if ((uc == 0 && dc == 0) ||
                                (cr == 0 && cl == 0)) {
                            out[y * width + x] = BLACK;
                        }
                    }
                }
            }

        }

    }

    public static class Contrast extends Filter {

        @Override
        protected void process(int[] in, int[] out, int width) {
            for (int i = 0; i < in.length; i++) {
                int pixel = in[i];
                int blue = (pixel & 0xff);

                int gray = blue > 50 ? 255 : 0;
                out[i] = rgb(gray);
            }
        }

    }

    public static abstract class Filter {
        public int scale(int x) {
            return x;
        }

        public BufferedImage apply(BufferedImage image) {
            int height = image.getHeight();
            int width = image.getWidth();

            int[] in = new int[width * height];
            image.getRGB(0, 0, width, height, in, 0, width);

            int scaleWidth = scaleWidth(width);
            int scaleHeight = scaleHeight(height);

            int[] out = new int[scaleWidth * scaleHeight];
            process(in, out, width);

            BufferedImage outImage = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_4BYTE_ABGR);
            outImage.setRGB(0, 0, scaleWidth, scaleHeight, out, 0, scaleWidth);
            return outImage;
        }

        protected int scaleHeight(int height) {
            return scale(height);
        }

        protected int scaleWidth(int width) {
            return scale(width);
        }

        protected abstract void process(int[] in, int[] out, int width);

        protected int bool(int[] in, int width, int x, int y) {
            return bool(in[y * width + x]);
        }

        int bool(int i) {
            return (i & 0xff) > 0 ? 1 : 0;
        }
    }

    protected static int rgb(int gray) {
        return (255 << 24) + (gray << 16) + (gray << 8) + gray;
    }

}
