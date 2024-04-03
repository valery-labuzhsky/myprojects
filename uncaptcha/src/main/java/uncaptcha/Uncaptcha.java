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

import static java.lang.Math.*;

public class Uncaptcha {

    public static final int WHITE = rgb(0xff);
    public static final int BLACK = rgb(0);

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File(args[0]));
        image = transformOld(image, Integer.parseInt(args[2]), Double.parseDouble(args[3]));
//        image = transform(image);
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
        image = new RemoveBalloons().apply(image);
        BufferedImage orig = image;
        image = new Count().apply(image);
        Rotate rotation = new FineFrame2();
        image = rotation.apply(image);
        image = orig;

        image = rotation.rotation().apply(image);

        image = new DoubleSlit(12).apply(image);
        FineFrame2 frame = new FineFrame2();
        image = frame.apply(image);
        rotation = rotation.combine(frame);
        image = rotation.apply(orig);

        image = new DoubleSlit(10).apply(image);
        frame = new FineFrame2();
        image = frame.apply(image);
        rotation = rotation.combine(frame);
        image = rotation.apply(orig);

        image = new DoubleSlit(8).apply(image);
        frame = new FineFrame2();
        image = frame.apply(image);
        rotation = rotation.combine(frame);
        image = rotation.apply(orig);

        orig = rotation.apply(orig);
        orig = new Cut().apply(orig);
        orig = new Window().apply(orig);
        orig = new Split2().apply(orig);
        orig = new RemoveBalloons().apply(orig);
        orig = new Shrink1().apply(orig);
        orig = new Shrink3().apply(orig);
        orig = new Compact().apply(orig);
        if (true) return orig;
        return orig;
    }

    public static class Split2 extends Filter {

        @Override
        protected void process(int[] in, int[] out, int width) {
            FullMatrix inm = new FullMatrix(in, width);
            ArrayList<Integer> borders = new ArrayList<>();
            int border = findStart(inm);
            borders.add(border - 5);
            while (borders.size() < 7) {
                border = findNext(border, inm);
                borders.add(border);
            }

            Arrays.fill(out, WHITE);

            double[] ks = new double[]{2, 1.2, 1, 0.8, 0.8, 0.8};
            for (int n = 0; n < 6; n++) {
                int sx = (borders.get(n + 1) + borders.get(n)) / 2;
                int lw = 33;
                int tx = 1 + n * lw + lw / 2;
                double k = ks[n];

                if (lw > (borders.get(n + 1) - borders.get(n)) * k)
                    lw = (int) ((borders.get(n + 1) - borders.get(n)) * k);
                for (int x = -lw / 2 + 1; x < lw / 2 + 1; x++) {
                    for (int y = 0; y < inm.getHeight(); y++) {
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

        private static int findNext(int lx, FullMatrix inm) {
            int lowX = lx + 10;
            int lowH = 40;
            for (int x = lx + 10; x < lx + 40; x++) {
                int up = inm.getHeight();
                for (int y = 0; y < inm.getHeight(); y++) {
                    if (inm.get(x, y) == BLACK) {
                        up = y;
                        break;
                    }
                }

                int down = up;
                for (int y = inm.getHeight() - 1; y > up; y--) {
                    if (inm.get(x, y) == BLACK) {
                        down = y;
                        break;
                    }
                }

                if ((down - up) < lowH) {
                    lowH = (down - up);
                    lowX = x;
                }
            }
            return lowX;
        }

        private static int findStart(FullMatrix inm) {
            for (int x = 0; x < inm.getWidth(); x++) {
                int up = inm.getHeight();
                for (int y = 0; y < inm.getHeight(); y++) {
                    if (inm.get(x, y) == BLACK) {
                        up = y;
                        break;
                    }
                }

                int down = up;
                for (int y = inm.getHeight() - 1; y > up; y--) {
                    if (inm.get(x, y) == BLACK) {
                        down = y;
                        break;
                    }
                }

                if ((down - up) > 35) {
                    return x;
                }
            }
            return inm.getWidth();
        }
    }


    public static String detect(String path) throws IOException {
        return detect(ImageIO.read(new File(path)));
    }

    public static String detect(BufferedImage image) {
        image = new Contrast().apply(image);
        image = new RemoveHoles().apply(image);
        image = new RemoveBalloons().apply(image);
        BufferedImage orig = image;
        image = new Count().apply(image);
        Rotate rotation = new FineFrame2();
        image = rotation.apply(image);
        image = orig;

        image = rotation.rotation().apply(image);

        image = new DoubleSlit(12).apply(image);
        FineFrame2 frame = new FineFrame2();
        image = frame.apply(image);
        rotation = rotation.combine(frame);
        image = rotation.apply(orig);

        image = new DoubleSlit(10).apply(image);
        frame = new FineFrame2();
        image = frame.apply(image);
        rotation = rotation.combine(frame);
        image = rotation.apply(orig);

        image = new DoubleSlit(8).apply(image);
        frame = new FineFrame2();
        image = frame.apply(image);
        rotation = rotation.combine(frame);
        image = rotation.apply(orig);

        orig = rotation.apply(orig);
        orig = new Cut().apply(orig);
        orig = new Window().apply(orig);
        orig = new Split2().apply(orig);
        orig = new RemoveBalloons().apply(orig);
        orig = new Shrink1().apply(orig);
        orig = new Shrink3().apply(orig);
        Compact compact = new Compact();
        orig = compact.apply(orig);
        return compact.numbers.toString();
    }

    public static class Shrink3 extends Filter {

        private FullMatrix outm;

        @Override
        public int scale(int x) {
            return x / 2;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            outm = new FullMatrix(out, scale(width));

            int sw = scale(width);
            int sh = scale(in.length / width);

            ArrayList<Integer> ones = new ArrayList<>();
            ArrayList<Integer> twos = new ArrayList<>();

            int[] square = new int[4];
            for (int x = 0; x < sw; x++) {
                for (int y = 0; y < sh; y++) {
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dy = 0; dy < 2; dy++) {
                            square[dy * 2 + dx] = bool(in[(y * 2 + dy) * width + x * 2 + dx]);
                        }
                    }
                    int sum = Arrays.stream(square).sum();
                    int j = y * sw + x;
                    if (sum < 4) {
                        out[j] = BLACK;
                    } else {
                        out[j] = WHITE;
                    }
                    switch (sum) {
                        case 3 -> ones.add(j);
                        case 2 -> twos.add(j);
                    }
                }
            }

            ones = process(ones, 2.1);
            twos = process(twos, 5);
            for (int i : ones) {
                outm.set(i, WHITE);
            }
        }

        private ArrayList<Integer> process(ArrayList<Integer> ones, double discard) {
            TreeMap<Double, ArrayList<Integer>> ranked = new TreeMap<>();
            for (int i : ones) {
                Fragment frag = new Fragment(outm, outm.getX(i) - 1, outm.getY(i) - 1, 3, 3);
                ranked.computeIfAbsent(frag.getPerimeter().mapToDouble(j -> {
                    if (frag.get(j) == WHITE) return 0;
                    else if (j % 2 == 0) return 1 / sqrt(2);
                    else return 1;
                }).sum(), r -> new ArrayList<>()).add(i);
            }

            ArrayList<Integer> tips = new ArrayList<>();

            if (ranked.containsKey(0d)) {
                for (int i : ranked.remove(0d)) {
                    outm.set(i, WHITE);
                }
            }

            {
                Map.Entry<Double, ArrayList<Integer>> dentry = ranked.higherEntry(discard);
                while (dentry != null) {
                    for (int i : dentry.getValue()) {
                        outm.set(i, WHITE);
                    }
                    ranked.remove(dentry.getKey());
                    dentry = ranked.higherEntry(discard);
                }
            }

            double tipRank = 1.1;
            {
                Map.Entry<Double, ArrayList<Integer>> dentry = ranked.lowerEntry(tipRank);
                while (dentry != null) {
                    for (int i : dentry.getValue()) {
                        outm.set(i, WHITE);
                    }
                    ranked.remove(dentry.getKey());
                    dentry = ranked.lowerEntry(tipRank);
                }
            }

            for (Map.Entry<Double, ArrayList<Integer>> entry : ranked.entrySet()) {
                for (Integer i : entry.getValue()) {
                    Fragment frag = new Fragment(outm, outm.getX(i) - 1, outm.getY(i) - 1, 3, 3);
                    if (!frag.isBridge()) {
                        outm.set(i, WHITE);
                    }
                }
            }

            return tips;
        }

    }

    public static class DoubleSlit extends Filter {

        private int slit;

        public DoubleSlit(int slit) {
            this.slit = slit;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            FullMatrix inm = new FullMatrix(in, width);
            FullMatrix outm = new FullMatrix(out, width);
            outm.fill(WHITE);

            int lh = 30;
            int height = inm.getHeight();

            for (int x = 0; x < width; x++) {
                for (int y = height / 2 - lh / 2 - slit; y < height / 2 - lh / 2 + slit / 2; y++) {
                    outm.set(x, y + (lh - slit) / 2, inm.get(x, y));
                    outm.set(x, height - y - 1 - (lh - slit) / 2, inm.get(x, height - y - 1));
                }
            }
        }
    }


    public static class FineFrame2 extends Rotate {

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
            FullMatrix inm = new FullMatrix(in, width);

            ArrayList<Integer> xs1 = new ArrayList<>();
            ArrayList<Integer> ys1 = new ArrayList<>();

            ArrayList<Integer> xs2 = new ArrayList<>();
            ArrayList<Integer> ys2 = new ArrayList<>();

            for (int x = 0; x < inm.getWidth() / 2; x++) {
                for (int y = 0; y < inm.getHeight(); y++) {
                    int c = 2 - (inm.get(x, y) & 0xFF) / 0x7E;
                    for (int i = 0; i < c; i++) {
                        xs1.add(x);
                        ys1.add(y);
                    }
                }
            }

            for (int x = inm.getWidth() / 2; x < inm.getWidth(); x++) {
                for (int y = 0; y < inm.getHeight(); y++) {
                    int c = 2 - (inm.get(x, y) & 0xFF) / 0x7E;
                    for (int i = 0; i < c; i++) {
                        xs2.add(x);
                        ys2.add(y);
                    }
                }
            }

            Collections.sort(xs1);
            Collections.sort(ys1);

            Collections.sort(xs2);
            Collections.sort(ys2);

            int x01 = xs1.get(xs1.size() / 2);
            int y01 = ys1.get(ys1.size() / 2);

            int x02 = xs2.get(xs2.size() / 2);
            int y02 = ys2.get(ys2.size() / 2);

            if (x01 < inm.getWidth() - x02) {
                x0 = x01;
                y0 = y01;
            } else {
                x0 = x02;
                y0 = y02;
            }
            x0 -= inm.getWidth() / 2d;
            y0 -= inm.getHeight() / 2d;

            ang = rang(Math.atan2(y02 - y01, x02 - x01));
        }
    }

    public static class RemoveBalloons extends Filter {
        private FullMatrix inm;
        HashSet<Integer> visited;
        double x0, x1, y0, y1;
        private HashSet<Integer> blob;

        @Override
        protected void process(int[] in, int[] out, int width) {
            inm = new FullMatrix(in, width);
            FullMatrix outm = new FullMatrix(out, width);
            outm.fill(WHITE);

            visited = new HashSet<>();
            for (int i = 0; i < in.length; i++) {
                if (!visited.contains(i)) {
                    visited.add(i);
                    if (inm.colors[i] == BLACK) {
                        blob = new HashSet<>();
                        blob.add(i);
                        int x = inm.getX(i);
                        int y = inm.getY(i);
                        x0 = x1 = x;
                        y0 = y1 = y;
                        visit(x, y);

                        if (Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)) > 35) {
                            for (Integer b : blob) {
                                outm.colors[b] = BLACK;
                            }
                        }
                    }
                }
            }
        }

        private void visit(int x, int y) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int i = inm.getI(x + dx, y + dy);
                    if (i >= 0 && !visited.contains(i)) {
                        visited.add(i);
                        if (inm.colors[i] == BLACK) {
                            blob.add(i);
                            if (x0 > x) x0 = x;
                            else if (x1 < x) x1 = x;
                            if (y0 > y) y0 = y;
                            else if (y1 < y) y1 = y;
                            visit(x + dx, y + dy);
                        }
                    }
                }
            }
        }
    }

    public static class Shadow extends Filter {
        @Override
        protected void process(int[] in, int[] out, int width) {
            FullMatrix inm = new FullMatrix(in, width);
            FullMatrix outm = new FullMatrix(out, width);
            outm.fill(WHITE);

            int high = 17;
            for (int x = 0; x < inm.getWidth(); x++) {
                for (int y = 0; y < inm.getHeight() - high * 2; y++) {
                    if (inm.get(x, y) == BLACK) {
                        if (y == 0) break;
                        outm.set(x, y + high, rgb((outm.get(x, y + high) & 0xFF) - 0x7E));
                        break;
                    }
                }
                for (int y = inm.getHeight() - 1; y >= high * 2; y--) {
                    if (inm.get(x, y) == BLACK) {
                        if (y == inm.getHeight() - 1) break;
                        outm.set(x, y - high, rgb((outm.get(x, y - high) & 0xFF) - 0x7E));
                        break;
                    }
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
            System.out.println(angs.size());
            System.out.println(ang);

            angs = new ArrayList<>(angs.stream().filter(a -> a > ang - Math.PI / 50 && a < ang + Math.PI / 50).toList());
            ang = angs.get(angs.size() / 2);
            System.out.println(angs.size());
            System.out.println(ang);

            angs = new ArrayList<>(angs.stream().filter(a -> a > ang - Math.PI / 150 && a < ang + Math.PI / 150).toList());
            ang = angs.get(angs.size() / 2);
            System.out.println(angs.size());
            System.out.println(ang);

            int cx = width / 2;
            int cy = in.length / width / 2;
            x0 -= cx;
            y0 -= cy;
        }
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

            int n = 0;

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
                        if (bool(in, width, ex + 1, y) == 0) {
                            found = true;
                            ex++;
                            break;
                        }
                    }
                    if (ex >= (n + 1) * width / 6) break;
                }
                n++;

                FullMatrix all = new FullMatrix(out, width);

                int ch = ey - sy + 1;
                int cw = ex - sx + 1;
                if (cw < ch * 3 / 5) cw = ch * 3 / 5;

                Fragment cipher = new Fragment(all, ex - cw + 1, sy, cw, ch);

                ch = compactToHeight(cipher, 5);
                cw = compactToHeight(new Transposed(cipher), 3);

                if (ch == 5 && cw == 3) {
                    Fragment number = new Fragment(all, ex - 3 + 1, ey - 5 + 1, 3, 5);
                    if (number.matches("""
                            x x x
                            x . x
                              x
                            x . x
                            x x x
                            """)) {
                        this.numbers.append('8');
                    } else if (number.matches("""
                            x x x
                            x . x
                            x x x
                            .   x
                            x x
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
                    } else if (number.matchesAny("""
                            x x x|x x x
                            x . x|x . x
                            x . x|x . x
                            x . x|x x x
                            x x x|\s
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
                            .
                            . x
                              x
                            """)) {
                        this.numbers.append('7');
                    } else if (number.matches("""
                            .   x
                            .   x
                            . . x
                            . . x
                            . . x
                            """)) {
                        this.numbers.append('1');
                    } else if (number.matchesAny("""
                              x x|.   x|. x x
                            x . x|  x x|x . x
                            x x x|x . x|x . x
                            . . x|  x x|x x x
                            . . x|. . x|. . x
                            """)) {
                        this.numbers.append('4');
                    } else {
                        this.numbers.append('?');
                    }
                } else if (ch == 7 && cw == 3) {
                    Fragment number = new Fragment(all, ex - 3 + 1, ey - 7 + 1, 3, 7);
                    if (number.matchesAny("""
                              x  |  x
                            x . x|x . x
                            x x x|x x x
                            . . x|x . .
                            x x x|x x x
                            x . x|x . x
                              x  |  x
                            """)) {
                        this.numbers.append('8');
                    } else {
                        this.numbers.append('?');
                    }
                } else {
                    this.numbers.append('?');
                }
            }

            System.out.println(numbers);
        }

        private int compactToHeight(Matrix cipher, int target) {
            int height = cipher.getHeight();
            int toCompact = height - target;
            double yc = 0;
            while (toCompact > 1) {
                int y = (int) yc;

                if (y >= cipher.getHeight() - 2) {
                    break;
                }

                if (canCompact(cipher, y)) {
                    compactLine(cipher, y);
                    yc += (height - 2d) / (toCompact - 1);
                    height--;
                    toCompact--;
                } else {
                    yc += 1;
                }
            }

            for (int y = cipher.getHeight() - 2; y >= cipher.getHeight() - height; y--) {
                if (canCompact(cipher, y)) {
                    compactLine(cipher, y);
                    height--;
                    toCompact--;
                    break;
                }
            }

            for (int y = cipher.getHeight() - height; toCompact > 0 && y < cipher.getHeight() - 1; y++) {
                if (canCompact(cipher, y)) {
                    compactLine(cipher, y);
                    height--;
                    toCompact--;
                }
            }
            return height;
        }

        private static boolean canCompact(Matrix cipher, int y) {
            for (int x = 0; x < cipher.getWidth(); x++) {
                String naughties = """
                          x  |  x  |  x x|x x  |. x  |  x .|
                        x . x|. . .|x . .|. . x|. .  |  . .|
                          x  |  x  |  x x|x x  |x .  |  . x|
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
                    if (yo == 0) cipher.set(x, yo, WHITE);
                    else cipher.set(x, yo, cipher.get(x, yo - 1));
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
                    if (Arrays.stream(square).sum() <= 2) {
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
            int[] dxs = new int[]{6, 26, 47, 75, 109, 142, 177};
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
            int l = 54;
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
            double vert = sin(ang);
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
            Rotate combine = absolute();
            rotate = rotate.absolute();
            combine.x0 += rotate.x0;
            combine.y0 += rotate.y0;
            combine.ang += rotate.ang;
            return combine.relative();
        }

        public Rotate absolute() {
            Rotate rotation = rotation();
//            rotation.x0 = x0 * cos(ang) - y0 * sin(ang) - x0;
//            rotation.y0 = x0 * sin(ang) + y0 * cos(ang) - y0;
//            rotation.x0 = x0 * cos(ang) - y0 * sin(ang) - x0;
//            rotation.y0 = x0 * sin(ang) + y0 * cos(ang);
            rotation.x0 = x0 * cos(ang) - y0 * sin(ang) - x0;
            rotation.y0 = x0 * sin(ang) + y0 * cos(ang) - y0 * 2;
            return rotation;
        }

        public Rotate relative() {
            Rotate rotation = rotation();

//            rotation.x0 = (-x0 + y0 * sin(ang) / (1 - cos(ang))) / 2;
//            rotation.y0 = (-x0 * sin(ang) / (1 - cos(ang)) - y0) / 2;

//            rotation.x0 = (x0 * cos(ang) + y0 * sin(ang)) / (1 - cos(ang));
//            rotation.y0 = (-x0 * sin(ang) + y0 * (cos(ang) - 1)) / (1 - cos(ang));

//            x0 * (cos(ang) - 2) + y0 * sin(ang) = x0 * (3 - 3 * cos(ang));

//            -x0 * sin(ang) + y0 * (cos(ang) - 1) = y0 * (3 - 3 * cos(ang));
//            y0 * (cos(ang) - 1) = y0 * (cos(ang) - 2) * (cos(ang) - 1);

            rotation.x0 = (x0 * (cos(ang) - 2) + y0 * sin(ang)) / (1 - cos(ang)) / 3;
            rotation.y0 = (-x0 * sin(ang) + y0 * (cos(ang) - 1)) / (1 - cos(ang)) / 3;

            return rotation;
        }

        @Override
        public String toString() {
            return "Rotate{" +
                    "x0=" + x0 +
                    ", y0=" + y0 +
                    ", ang=" + ang +
                    '}';
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
