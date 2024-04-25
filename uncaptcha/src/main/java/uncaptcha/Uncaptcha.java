package uncaptcha;

import uncaptcha.matrix.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Math.*;

public class Uncaptcha {

    public static final int WHITE = rgb(0xff);
    public static final int GRAY = rgb(0x7e);
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
//        if (true) return image;
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
        orig = new RemoveBalloons().apply(orig);
        orig = new Split3().apply(orig);
        orig = new RemoveBalloons().apply(orig);
        orig = new Shrink1().apply(orig);
        orig = new Shrink4().apply(orig);
        orig = new Compact3().apply(orig);
        if (true) return orig;
        return orig;
    }

    public static class Compact3 extends Filter {
        StringBuilder numbers = new StringBuilder();
        private int n;
        private Fragment cipher;
        private Fragment cout;


        @Override
        protected void process(int[] in, int[] out, int width) {
            FullMatrix inm = new FullMatrix(in, width);
            FullMatrix outm = new FullMatrix(out, width);
            outm.fill(WHITE);

            int sx = 0;
            int sy = 0;
            int ex = -1;
            int ey = 0;


            n = 0;

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
                    if (sy > 0) {
                        for (int x = sx; x <= ex + 1; x++) {
                            if (bool(in, width, x, sy - 1) == 0) {
                                found = true;
                                sy--;
                                break;
                            }
                        }
                    }
                    if (ey < in.length / width - 1) {
                        for (int x = sx; x <= ex + 1; x++) {
                            if (inm.get(x, ey + 1) == BLACK) {
                                found = true;
                                ey++;
                                break;
                            }
                        }
                    }
                    if (ex < (n + 1) * width / 6 && ex < width - 1) {
                        for (int y = sy; y <= ey + 1; y++) {
                            if (bool(in, width, ex + 1, y) == 0) {
                                found = true;
                                ex++;
                                break;
                            }
                        }
                    }
                }
                n++;

                while (ey - sy + 1 > 10) {
                    int sc = 0;
                    int ec = 0;
                    for (int x = sx; x <= ex; x++) {
                        if (inm.get(x, sy) == BLACK) {
                            sc++;
                        }
                    }
                    for (int x = sx; x <= ex; x++) {
                        if (inm.get(x, ey) == BLACK) {
                            ec++;
                        }
                    }
                    if (sc > ec) {
                        for (int x = sx; x <= ex; x++) {
                            if (inm.get(x, ey) == BLACK) {
                                inm.set(x, ey - 1, BLACK);
                                inm.set(x, ey, WHITE);
                            }
                        }
                        ey--;
                    } else {
                        for (int x = sx; x <= ex; x++) {
                            if (inm.get(x, sy) == BLACK) {
                                inm.set(x, sy + 1, BLACK);
                                inm.set(x, sy, WHITE);
                            }
                        }
                        sy++;
                    }
                }
                if (ey - sy + 1 < 10) sy = ey + 1 - 10;
                if (sy < 0) {
                    ey -= sy;
                    sy = 0;
                }

                while (ex - sx + 1 > 6) {
                    int sc = 0;
                    int ec = 0;
                    for (int y = sy; y <= ey; y++) {
                        if (inm.get(sx, y) == BLACK) {
                            sc++;
                        }
                    }
                    for (int y = sy; y <= ey; y++) {
                        if (inm.get(ex, y) == BLACK) {
                            ec++;
                        }
                    }
                    if (sc > ec) {
                        for (int y = sy; y <= ey; y++) {
                            if (inm.get(ex, y) == BLACK) {
                                inm.set(ex - 1, y, BLACK);
                                inm.set(ex, y, WHITE);
                            }
                        }
                        ex--;
                    } else {
                        for (int y = sy; y <= ey; y++) {
                            if (inm.get(sx, y) == BLACK) {
                                inm.set(sx + 1, y, BLACK);
                                inm.set(sx, y, WHITE);
                            }
                        }
                        sx++;
                    }
                }
                if (ex - sx + 1 < 6) sx = ex + 1 - 6;

                int ch = ey - sy + 1;
                int cw = ex - sx + 1;

                cipher = new StrictFragment(inm, sx, sy, cw, ch);
                cout = new StrictFragment(outm, sx, sy, 3, 5);

                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 5; y++) {
                        cout.set(x, y, GRAY);
                    }
                }

                forAllCells(cipher, cout, s -> {
                    evolve(s);
                });

                forAllCells(cipher, cout, s -> {
                    switch (s.in.count()) {
                        case 2:
                        case 3:
                            s.out.set(0, 0, BLACK);
                    }
                });

                if (cout.matches("""
                        . x .
                        x   x
                        . x .
                        x   x
                        . x .
                        """)) {
                    this.numbers.append('8');
                } else if (cout.matchesAny("""
                        . x .|. x .
                        x   x|x   x
                        .   x|. x x
                          x x|    x
                        x x .|. x .
                        """)) {
                    this.numbers.append('9');
                } else if (cout.matchesAny("""
                        . x .|. x x
                        x .  |x x \s
                        x x .|x   .
                        x   x|x   x
                        . x .|. x .
                        """)) {
                    this.numbers.append('6');
                } else if (cout.matches("""
                        . x .
                        x   x
                        x   x
                        x   x
                        . x .
                        """)) {
                    this.numbers.append('0');
                } else if (cout.matches("""
                        x x .
                        x   \s
                        . x .
                            x
                        . x .
                        """)) {
                    this.numbers.append('5');
                } else if (cout.matchesAny("""
                        . x .|. x x
                            x|.   x
                        . x x|  . x
                        x    |. x \s
                        x x x|. x x
                        """)) {
                    this.numbers.append('2');
                } else if (cout.matchesAny("""
                        . x .|. x x
                            x|.   x
                        . x .|    x
                            x|x   x
                        . x .|. x .
                        """)) {
                    this.numbers.append('3');
                } else if (cout.matchesAny("""
                        x x .|x x x|x x \s
                            x|  x .|  x x
                          . .|  x  |    x
                          x  |. x  |  x x
                        . .  |x    |x x \s
                        """)) {
                    this.numbers.append('7');
                } else if (cout.matchesAny("""
                          . .|. . \s
                          . .|. . .
                          . .|  . .
                          . .|  . .
                          . .|  . .
                        """)) {
                    this.numbers.append('1');
                } else if (cout.matchesAny("""
                          . .|  . .|  x .|  x x|  x x
                        . x .|  . .|x   x|  x x|x   x
                        x   x|  x .|x   x|x   x|x x x
                        . x .|x   x|x x x|x   x|  x x
                        . . x|. x .|    x|  x  |  x x
                        """)) {
                    this.numbers.append('4');
                } else {
                    this.numbers.append('?');
                }
            }

            System.out.println(numbers);
        }

        private void evolve(Strider s) {
            log(s.x + ", " + s.y);
            log(cipher);
//            log(cout);
            switch (s.in.count()) {
                case 0:
                    s.out.set(0, 0, WHITE);
                    break;
                case 1:
                    if (!s.tryExpand()) s.tryCollapse();
                    break;
                case 2:
                    if (s.in.matchesAny("""
                              x|  \s
                              x|x x
                            """)) {
                        if (!s.tryExpand() && !s.tryCollapse()) {
                            s.findInMatchingRotation("""
                                      \s
                                    x x
                                    """, s1 -> {
                                if (s1.withForce(1).move(0, -1).tryMegaCollapse()) {
                                    // TODO need to make it in one transaction?
                                    s1.tryExpand();
                                } else {
                                    s1.withForce(1).cutCorners().tryCollapse();
                                }
                            });
                        }
                    } else if (s.in.matchesAny("""
                            x  |x x
                            x  |  \s
                            """)) {
                        if (!s.tryExpand() && !s.tryCollapse()) {
                            s.withForce(1).cutCorners().tryExpand();
                        }
                    } else {
                        s.tryExpand();
                    }
                    break;
                case 3:
                    if (!s.tryExpand() && !s.tryCollapse()) {
                        if (!s.withForce(1).cutCorners().tryExpand()) {
                            s.withForce(1).cutCorners().tryCollapse();
                        }
                    }
                    break;
                case 4:
                    s.out.set(0, 0, BLACK);
                    break;
            }
        }

        private void forAllCells(Matrix cipher, Matrix cout, Consumer<Strider> consumer) {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 3; x++) {
                    Strider strider = createStrider(cipher, cout, x, y);
                    consumer.accept(strider);
                }
            }
        }

        private Strider createStrider(Matrix cipher, Matrix cout, int x, int y) {
            Fragment cell = new Fragment(cipher, x * 2, y * 2, 2, 2);
            Fragment cout0 = new Fragment(cout, x, y, 1, 1);

            return new Strider(cell, cout0, 0, false, x, y);
        }

        public class Strider {
            private final Matrix in;
            private final Matrix out;
            private final int force;
            private final boolean cutCorners;
            private final int x;
            private final int y;

            private final int cx;
            private final int cy;

            public Strider(Matrix in, Matrix out, int force, boolean cutCorners, int x, int y) {
                this(in, out, force, cutCorners, x, y, 0, 0);
            }

            public Strider(Matrix in, Matrix out, int force, boolean cutCorners, int x, int y, int cx, int cy) {
                this.in = in;
                this.out = out;
                this.force = force;
                this.cutCorners = cutCorners;
                this.x = x;
                this.y = y;
                this.cx = cx;
                this.cy = cy;
            }

            public boolean findInMatchingRotation(String pattern, Consumer<Strider> action) {
                for (Rotation r : Rotation.values()) {
                    if (r.t.apply(in).matches(pattern)) {
                        action.accept(transform(r.t));
                        return true;
                    }
                }
                return false;
            }

            public void forEachInMatchingRotation(String pattern, Consumer<Strider> action) {
                for (Rotation r : Rotation.values()) {
                    Matrix apply = r.t.apply(in);
//                    log(apply);
                    if (apply.matches(pattern)) {
                        action.accept(transform(r.t));
                    }
                }
            }

            public void forAllSymmetries(Symmetry symmetry, Consumer<Strider> action) {
                action.accept(this);
                action.accept(transform(symmetry.t));
            }

            private Strider transform(Function<Matrix, Matrix> t) {
                return new Strider(t.apply(in), t.apply(out), force, cutCorners, x, y);
            }

            public boolean change(int dx, int dy, String from, String to) {
                return move(dx, dy).ifOutMatches(from, s -> s.fit(to));
            }

            public void change(Symmetry symmetry, int dx, int dy, String from, String to) {
                forAllSymmetries(symmetry, s -> s.change(dx, dy, from, to));
            }

            public Strider move(int dx, int dy) {
                return new Strider(new Fragment(in, dx * 2, dy * 2, 2, 2),
                        new Fragment(out, dx, dy, 1, 1), force, cutCorners, x, y);
            }

            public boolean ifOutMatches(String pattern, Consumer<Strider> action) {
                if (out.matches(pattern)) {
                    action.accept(this);
                    return true;
                } else {
                    return false;
                }
            }

            public void fit(String pattern) {
                Matrix template = Matrix.create(pattern);
                template.forAllXY((x, y) -> {
                    if (template.get(x, y) == WHITE) {
                        move(x, y).collapse();
                    }
                });
                template.forAllXY((x, y) -> {
                    if (template.get(x, y) == BLACK) {
                        move(x, y).expand();
                    }
                });
            }

            public boolean tryCollapse() {
                Fragment reach = new Fragment(in, -force, -force, 2 + force * 2, 2 + force * 2);
                String backup = reach.toString();
                try {
                    collapse();
                    return true;
                } catch (RuntimeException e) {
                    reach.set(backup);
                    return false;
                }
            }

            public void log(Object o) {
                if (x == 1 && y == 1) {
                    Compact3.this.log(o);
                }
            }

            public boolean tryExpand() {
                Fragment reach = new Fragment(in, -force, -force, 2 + force * 2, 2 + force * 2);
                String backup = reach.toString();
                try {
                    expand();
                    return true;
                } catch (RuntimeException e) {
                    reach.set(backup);
                    return false;
                }
            }

            public void collapse() {
                findInMatchingRotation("""
                        x
                        \s
                        """, s1 -> {
                    s1.forEachInMatchingRotation("""
                            x
                            \s
                            """, Strider::tryCollapseCorner);
                });

                transform(Symmetry.TRANSPOSED.t).findInMatchingRotation("""
                        x
                        \s
                        """, s1 -> {
                    s1.forEachInMatchingRotation("""
                            x
                            \s
                            """, Strider::tryCollapseCorner);
                });

                if (in.count() != 0) throw new RuntimeException("Couldn't");
                out.set(0, 0, WHITE);
            }

            public void collapseCorner() {
                Fragment up = new Fragment(in, -1 + cx, -1 + cy, 3, 3);
                log("Collapse before "+force);
                log(new Fragment(up, -1, -1, 5, 5));
                if (!cutCorners && up.matchesAny("""
                             |    \s
                          x x|x x \s
                             |    \s
                        """
                )) {
                    throw new RuntimeException("Corner");
                }
                if (up.matchesAny("""
                        . . .
                        . x \s
                        . . x
                        """)) {
                    corner(1, 0).expandCorner();
                }
                if (up.matchesAny("""
                        . . .
                          x .
                        x . .
                        """)) {
                    corner(-1, 0).expandCorner();
                }
                if (up.matchesAny("""
                        .   .|.   x
                        . x x|. x .
                        """) && up.matchesAny("""
                        .   .|x   .
                        x x .|. x .
                        """)) {
                    corner(0, -1).expandCorner();
                }
                if (up.matchesAny("""
                        .   x
                        . x \s
                        . . .
                        """)) {
                    corner(1, 0).expandCorner();
                }
                if (up.matchesAny("""
                        x   .
                          x .
                        . . .
                        """)) {
                    corner(-1, 0).expandCorner();
                }
//                forAllSymmetries(Symmetry.TRANSPOSED, f2 -> {
//                    Fragment up = new Fragment(f2.in, 0, -1, 2, 2);
//                    if (up.matchesAny("""
//                              x|  .
//                            x .|x x
//                            """)) {
//                        Fragment left = new Fragment(up, -1, 0, 2, 3);
//                        // just remove it when it's not needed
//                        if (!cutCorners || !left.matches("""
//                                  \s
//                                  x
//                                  \s
//                                """)) {
//                            f2.corner(0, -1).expandCorner();
//                        }
//                    }
//                });
                this.in.set(cx, cy, WHITE);
                log("Collapse after");
                log(up);
                if (out.get(0, 0)==BLACK) collapse();
            }

            private boolean tryCollapseCorner() {
                Fragment reach = new Fragment(in, -force, -force, 2 + force * 2, 2 + force * 2);
                String save = reach.toString();
                try {
                    collapseCorner();
                    return true;
                } catch (RuntimeException e) {
                    reach.set(save);
                    return false;
                }
            }


            public boolean tryMegaCollapse() {
                Fragment reachIn = new Fragment(in, -force * 2, -force * 2, 2 + force * 4, 2 + force * 4);
                Fragment reachOut = new Fragment(in, -force, -force, 1 + force * 2, 1 + force * 2);
                String saveIn = reachIn.toString();
                String saveOut = reachOut.toString();
                try {
                    megaCollapse();
                    return true;
                } catch (RuntimeException e) {
                    reachIn.set(saveIn);
                    reachOut.set(saveOut);
                    return false;
                }
            }

            public void megaCollapse() {
                Fragment bigIn = new Fragment(in, -1, -1, 4, 4);
                Fragment bigOut = new Fragment(in, -1, -1, 3, 3);
                if (bigIn.matches("""
                        . . . .
                        . x x .
                        . x x .
                        . . . x
                        """)) {
                    if (bigOut.matches("""
                            . . .
                            . x \s
                            . . .
                            """)) {
                        corner(2, 0).megaExpand();
                    } else {
                        corner(2, 0).expand();
                    }
                }
                if (bigIn.matches("""
                        . . . .
                        . x x .
                        . x x .
                        x . . .
                        """)) {
                    if (bigOut.matches("""
                            . . .
                              x .
                            . . .
                            """)) {
                        corner(-2, 0).megaExpand();
                    } else {
                        corner(-2, 0).expand();
                    }
                }
                if (bigOut.matchesAny("""
                        x . .|. . .
                        . x .|x x .
                        . . .|. . .
                        """) && bigOut.matchesAny("""
                        . . x|. . .
                        . x .|. x x
                        . . .|. . .
                        """)) {
                    corner(0, -2).megaExpand();
                }
                this.out.set(0, 0, WHITE);
                this.in.set("""
                          \s
                          \s
                        """);
            }

            public void megaExpand() {
                Fragment up = new Fragment(out, -1, -1, 3, 2);
                if (up.matches("""
                        . x .
                        .   .
                        """)) {
                    corner(0, -2).collapseCorner();
                }
                if (up.matches("""
                        .   x
                        .   \s
                        """)) {
                    corner(2, -2).collapseCorner();
                }
                if (up.matches("""
                        x   .
                            .
                        """)) {
                    corner(-2, -2).collapseCorner();
                }
                this.out.set(0, 0, BLACK);
                this.in.set("""
                        x x
                        x x
                        """);
            }


            private Strider corner(int dx, int dy) {
                if (force == 0) {
                    throw new RuntimeException("Out of force");
                } else {
                    dx += cx;
                    int bx = 0;
                    while (dx < 0) {
                        bx--;
                        dx += 2;
                    }
                    while (dx > 1) {
                        bx++;
                        dx -= 2;
                    }

                    dy += cy;
                    int by = 0;
                    while (dy < 0) {
                        by--;
                        dy += 2;
                    }
                    while (dy > 1) {
                        by++;
                        dy -= 2;
                    }

                    return new Strider(new Fragment(in, bx * 2, by * 2, 2, 2), new Fragment(out, bx, by, 1, 1), force - 1, cutCorners, x, y, dx, dy);
                }
            }

            public void expand() {
                switch (in.count()) {
                    case 1:
                        findInMatchingRotation("""
                                  \s
                                  x
                                """, s1 -> {
                            s1.transform(Rotation.RIGHT.t).expandCorner();
                            s1.expandCorner();
                            s1.transform(Symmetry.X.t).expandCorner();
                        });
                        break;
                    case 2:
                        findInMatchingRotation("""
                                  \s
                                x x
                                """, s1 -> {
                            s1.expandCorner();
                            s1.transform(Symmetry.X.t).expandCorner();
                        });
                        findInMatchingRotation("""
                                  x
                                x \s
                                """, s1 -> {
                            s1.expandCorner();
                            s1.transform(Rotation.AROUND.t).expandCorner();
                        });
                        break;
                    case 3:
                        findInMatchingRotation("""
                                  x
                                x x
                                """, s1 -> {
                            s1.expandCorner();
                        });
                        break;
                }

//                findInMatchingRotation("""
//                        \s
//                        x
//                        """, s1 -> {
//                    s1.forEachInMatchingRotation("""
//                            \s
//                            x
//                            """, Strider::tryExpandCorner);
//                });

//                transform(Symmetry.TRANSPOSED.t).findInMatchingRotation("""
//                        \s
//                        x
//                        """, s1 -> {
//                    s1.forEachInMatchingRotation("""
//                            \s
//                            x
//                            """, Strider::tryExpandCorner);
//                });
                if (in.count() != 4) throw new RuntimeException("Couldn't");

//                forEachInMatchingRotation("""
//                        \s
//                        x
//                        """, Strider::expandCorner);
//                forEachInMatchingRotation("""
//                        \s
//                        x
//                        """, Strider::expandCorner);
                out.set(0, 0, BLACK);
            }

            private boolean tryExpandCorner() {
                Fragment reach = new Fragment(in, -force, -force, 2 + force * 2, 2 + force * 2);
                String save = reach.toString();
                try {
                    expandCorner();
                    return true;
                } catch (RuntimeException e) {
                    reach.set(save);
                    return false;
                }
            }

//            x x x x x x
//            x x v x x x
//            x x     x
//            x x x   x
//            x x   x x
//            x       x x
//            x       x
//            x x   x x
//            x x x x x
//            x x x

            public void expandCorner() {
                // TODO I assume that I have a black square down
                Fragment up = new Fragment(in, -1 + cx, -1 + cy, 3, 3);
                log("Expand Before "+force);
                log(up);
                if (!cutCorners && up.matchesAny("""
                        . x x|x x .
                        x    |    x
                        . x x|x x .
                        """
                )) {
                    throw new RuntimeException("Corner");
                }
                if (up.matches("""
                        . x .
                        x   x
                        """)) {
                    corner(0, -1).collapseCorner();
                }
                if (up.matches("""
                        . x .
                            \s
                        """)) {
                    corner(0, -1).collapseCorner();
                }
                if (up.matches("""
                        .   x
                        .   \s
                        """)) {
                    corner(1, -1).collapseCorner();
                }
                if (up.matches("""
                        x   .
                            .
                        """)) {
                    corner(-1, -1).collapseCorner();
                }
                this.in.set(cx, cy, BLACK);
                log("Expand After");
                log(up);
                if (out.get(0, 0)==WHITE) {
                    expand();
                }
            }

            public Strider withForce(int force) {
                return new Strider(in, out, force, cutCorners, x, y);
            }

            public Strider cutCorners() {
                return new Strider(in, out, force, true, x, y);
            }

        }

        private void log(Object log) {
//            if (n == 4) System.out.println(log);
        }

        public enum Rotation {
            ZERO(m -> m),
            RIGHT(m -> new XReflected(new Transposed(m))),
            AROUND(m -> new XReflected(new YReflected(m))),
            LEFT(m -> new YReflected(new Transposed(m)));

            Function<Matrix, Matrix> t;

            Rotation(Function<Matrix, Matrix> t) {
                this.t = t;
            }
        }

        public enum Symmetry {
            TRANSPOSED(m -> new Transposed(m)),
            X(m -> new XReflected(m)),
            Y(m -> new YReflected(m));

            Function<Matrix, Matrix> t;

            Symmetry(Function<Matrix, Matrix> t) {
                this.t = t;
            }
        }
    }


    public static class Split3 extends Filter {

        private FullMatrix inm;

        @Override
        protected void process(int[] in, int[] out, int width) {
            inm = new FullMatrix(in, width);
            FullMatrix outm = new FullMatrix(out, width);

            LinkedList<Cipher> ciphers0 = detectCiphers(0, inm.getWidth() - 1, 0, inm.getHeight() * 2 / 3);
            mergeIntersections(ciphers0);

//            Cipher toCheck = ciphers0.get(0);
//            ciphers0.removeIf(c -> c != toCheck);

            LinkedList<Cipher> ciphers = new LinkedList<>();
//            ciphers = ciphers0;
            for (Cipher cipher : ciphers0) {
                ciphers.addAll(detectCiphers(cipher.start - 5, cipher.end + 5, cipher.high, cipher.high + 30));
            }
            mergeIntersections(ciphers);

            for (Iterator<Cipher> iterator = ciphers.iterator(); iterator.hasNext(); ) {
                Cipher cipher = iterator.next();
                if (cipher.start==0) {
                    iterator.remove();
                    continue;
                }
                y:
                for (int y = inm.getHeight() - 1; y > cipher.high; y--) {
                    for (int x = cipher.start; x <= cipher.end; x++) {
                        if (inm.get(x, y) == BLACK) {
                            if (y - cipher.high < 35) iterator.remove();
                            break y;
                        }
                    }
                }
            }

            while (ciphers.size() < 6) {
                Cipher fat = ciphers.get(0);
                for (Cipher c : ciphers) {
                    if (fat.end - fat.start < c.end - c.start) {
                        fat = c;
                    }
                }

                int lowY = fat.high;
                int lowX = (fat.start + fat.end) / 2;
                for (int x = fat.start + (fat.end - fat.start) / 3; x < fat.end - (fat.end - fat.start) / 3; x++) {
                    for (int y = fat.high; y < inm.getHeight(); y++) {
                        if (inm.get(x, y) == BLACK || y == inm.getHeight() - 1) {
                            if (y > lowY) {
                                lowY = y;
                                lowX = x;
                            }
                            break;
                        }
                    }
                }

                Cipher child = new Cipher();
                child.start = lowX;
                child.end = fat.end;
                child.high = fat.high;
                child.highX = (child.start + child.end) / 2;
                fat.end = lowX;
                ciphers.add(ciphers.indexOf(fat) + 1, child);

                fat.findNewHigh();
                child.findNewHigh();
            }

//            System.arraycopy(in, 0, out, 0, out.length);
//            for (Cipher c : ciphers) {
//                for (int y = 0; y < inm.getHeight(); y++) {
//                    out[y * width + c.start] = (255 << 24) + (0xFF << 16);
//                    out[y * width + c.end] = (255 << 24) + (0xFF << 8);
//                }
//            }

            for (int x = 0; x < inm.getWidth(); x++) {
                for (int y = 0; y < inm.getHeight(); y++) {
                    int ci = x * 6 / inm.getWidth();

                    double[] ks = new double[]{2, 1.2, 1, 0.8, 0.8, 0.8};
                    Cipher c = ciphers.get(ci);
                    int ty = (int) (y - inm.getHeight() / 2d + 45 / 2d);
                    double txd = x - (2d * ci + 1) * inm.getWidth() / 6 / 2;
                    txd /= ks[ci];
                    txd += (c.start + c.end) / 2d;
                    int tx = (int) txd;

                    if (ty < 0 || ty >= 45 || tx < c.start || tx > c.end) {
                        outm.set(x, y, WHITE);
                    } else {
                        outm.set(x, y, inm.get(tx, ty + c.high));
                    }
                }
            }
        }

        private LinkedList<Cipher> detectCiphers(int sx, int ex, int sy, int ey) {
            if (sx < 0) sx = 0;
            if (ex > inm.getWidth() - 1) ex = inm.getWidth() - 1;
            LinkedList<Cipher> ciphers = new LinkedList<>();
            LinkedList<Cipher> locals = new LinkedList<>();

            for (int y = sy; y < ey; y++) {
//                System.out.println(y + ": " + ciphers);
                int startX = sx;
                int endX = ex;
                ListIterator<Cipher> cit = locals.listIterator();
                ListIterator<Cipher> mit = ciphers.listIterator();
                Cipher next = null;
                Cipher mext = null;
                while (startX <= endX) {
                    Cipher prev = next;
                    Cipher mrev = mext;
                    next = null;
                    if (prev != null) {
                        startX = prev.end + 1;
                    }

                    while (cit.hasNext()) {
                        next = cit.next();
                        mext = mit.next();
                        int x = next.start;
                        if (inm.get(x, y) == BLACK) {
                            next.start = startX;
                            for (x = x - 1; x >= startX; x--) {
                                if (inm.get(x, y) == WHITE) {
                                    next.start = x + 1;
                                    break;
                                }
                            }
                            if (mext.start > next.start) mext.start = next.start;
                            endX = next.start - 1;
                        } else {
                            endX = next.start - 1;
                            next.start = next.end + 1;
                            for (x = x + 1; x <= next.end; x++) {
                                if (inm.get(x, y) == BLACK) {
                                    next.start = x;
                                    break;
                                }
                            }
                            if (next.start > next.end) {
                                cit.remove();
                                mit.remove();
                                next = null;
                                continue;
                            }
                        }
                        break;
                    }

                    if (prev != null) {
                        int x = prev.end;
                        if (inm.get(x, y) == BLACK) {
                            prev.end = endX;
                            for (x = x + 1; x <= endX; x++) {
                                if (inm.get(x, y) == WHITE) {
                                    prev.end = x - 1;
                                    break;
                                }
                            }
                            startX = prev.end + 1;
                            if (mrev.end < prev.end) mrev.end = prev.end;
                            if (next != null && prev.end + 1 >= next.start) {
                                prev.end = next.end;
                                mrev.end = mext.end;
                                if (prev.high > next.high) {
                                    prev.high = next.high;
                                    prev.highX = next.highX;
                                    mrev.high = mext.high;
                                    mrev.highX = mext.highX;
                                }
                                if (mrev.start > mext.start) {
                                    mrev.start = mext.start;
                                }
                                cit.remove();
                                mit.remove();
                                next = prev;
                                mext = mrev;
                                endX = ex;
                                continue;
                            }
                        } else {
                            for (x = x - 1; x > prev.start; x--) {
                                if (inm.get(x, y) == BLACK) {
                                    prev.end = x;
                                    break;
                                }
                            }
                        }
                    }


                    Cipher novo = null;
                    Cipher movo = null;
                    for (int x = startX; x <= endX; x++) {
                        if (inm.get(x, y) == BLACK) {
                            if (novo == null) {
                                novo = new Cipher();
                                novo.start = x;
                                novo.end = x;
                                novo.high = y;
                                novo.highX = x;
                                movo = novo.clone();
                                if (next == null) {
                                    cit.add(novo);
                                    mit.add(movo);
                                } else {
                                    cit.previous();
                                    cit.add(novo);
                                    cit.next();
                                    mit.previous();
                                    mit.add(movo);
                                    mit.next();
                                }
                            }
                        } else {
                            if (novo != null) {
                                novo.end = x - 1;
                                movo.end = novo.end;
                                novo = null;
                            }
                        }
                    }

                    startX = endX + 1;
                    endX = ex;
                }
            }
            return ciphers;
        }

        private static void mergeIntersections(LinkedList<Cipher> ciphers) {
            Cipher next;
            Cipher prev = null;
            for (ListIterator<Cipher> iterator = ciphers.listIterator(); iterator.hasNext(); ) {
                next = iterator.next();
                if (next.end - next.start < 10 && next.start > 50) {
                    iterator.remove();
                    continue;
                }
                if (prev != null) {
                    if (prev.end > next.start) {
                        prev.start = min(prev.start, next.start);
                        prev.end = max(prev.end, next.end);
                        if (prev.high > next.high) {
                            prev.high = next.high;
                            prev.highX = next.highX;
                        }
                        iterator.remove();
                        iterator.previous();
                        if (iterator.hasPrevious()) {
                            prev = iterator.previous();
                            iterator.next();
                        } else {
                            prev = null;
                        }
                        continue;
                    }
                }
                prev = next;
            }
        }

        public class Cipher implements Cloneable {
            int start;
            int end;
            int high;
            int highX;

            private void findNewHigh() {
                high = inm.getHeight();
                highX = (start + end) / 2;
                for (int x = start; x <= end; x++) {
                    for (int y = 0; y < inm.getHeight(); y++) {
                        if (inm.get(x, y) == BLACK) {
                            if (y < high) {
                                high = y;
                                highX = x;
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public Cipher clone() {
                try {
                    return (Cipher) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }

            public String toString() {
                return start + "-" + end;
            }
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
        orig = new RemoveBalloons().apply(orig);
        orig = new Split3().apply(orig);
        orig = new RemoveBalloons().apply(orig);
        orig = new Shrink1().apply(orig);
        orig = new Shrink4().apply(orig);
        Compact3 compact = new Compact3();
        orig = compact.apply(orig);
        return compact.numbers.toString();
    }

    public static class Shrink4 extends Filter {

        private FullMatrix outm;

        @Override
        public int scale(int x) {
            return x / 2;
        }

        @Override
        protected void process(int[] in, int[] out, int width) {
            outm = new FullMatrix(out, scale(width));
            FullMatrix inm = new FullMatrix(in, width);

            for (int x = 0; x < outm.getWidth(); x++) {
                for (int y = 0; y < outm.getHeight(); y++) {
                    Fragment square = new Fragment(inm, x * 2, y * 2, 2, 2);
                    int count = square.count();
                    if (count >= 3) {
                        outm.set(x, y, BLACK);
                    } else if (count == 0) {
                        outm.set(x, y, WHITE);
                    } else {
                        boolean bridge = false;
                        frag:
                        for (int x1 = x * 2; x1 < x * 2 + 2; x1++) {
                            for (int y1 = y * 2; y1 < y * 2 + 2; y1++) {
                                if (inm.get(x1, y1) == BLACK) {
                                    Fragment frag = new Fragment(inm, x1 - 1, y1 - 1, 3, 3);
                                    if (frag.isBridge()) {
                                        bridge = true;
                                        break frag;
                                    } else if (frag.count() != 2) {
                                        inm.set(x1, y1, WHITE);
                                    }
                                }
                            }
                        }
                        if (bridge) {
                            outm.set(x, y, BLACK);
                        } else {
                            outm.set(x, y, WHITE);
                        }
                    }
                }
            }
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

            int ix = (xs1.size() + xs2.size()) / 2;
            if (ix < xs1.size()) {
                x0 = xs1.get(ix);
            } else {
                x0 = xs2.get(ix - xs1.size());
            }

            ArrayList<Integer> ys = new ArrayList<>();
            ys.addAll(ys1);
            ys.addAll(ys2);
            Collections.sort(ys);
            y0 = ys.get(ys.size() / 2);
//            if (x01 < inm.getWidth() - x02) {
//                x0 = x01;
//                y0 = y01;
//            } else {
//                x0 = x02;
//                y0 = y02;
//            }

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
                            angs.add(rang(Math.atan2(x1, y)));
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
                            angs.add(rang(Math.atan2(x1, y)));
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
                    angs.add(rang(Math.atan2(x, y)));
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

                int gray = blue > 100 ? 255 : 0;
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
