package uncaptcha;

import uncaptcha.matrix.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static uncaptcha.Uncaptcha.*;

public abstract class SuperCompact extends Uncaptcha.Filter {
    protected int n;
    protected Matrix cipher;
    protected Matrix cout;

    protected void evolve(Strider s) {
        int count = s.in.count();
        if (count!=0 && count!=4) {
            log(s.x + ", " + s.y);
            log(cipher);
        }
//            log(cout);
        switch (count) {
            case 0:
                s.out.set(0, 0, Uncaptcha.WHITE);
                break;
            case 1:
                if (isCorner(s)) {
                    if (!s.withForce(2).tryExpand()) {
                        s.withForce(1).tryCollapse();
//                        s.tryIt(() -> {
//                            s.findInMatchingRotation("x", s1 -> {
//                                s1.withForce(1).corner(0, -1).expandCorner();
//                                s1.transform(Symmetry.TRANSPOSED.t).withForce(1).corner(0, -1).expandCorner();
//                                s1.in.set(0, 0, WHITE);
//                            });
//                            s.out.set(0, 0, WHITE);
//                        });
                    }
                } else {
                    if (!s.tryCollapse() && !s.tryExpand()) {
                        if (!s.withForce(1).tryCollapse()) {
                            s.withForce(1).tryExpand();
                        }
                    }
                }
                break;
            case 2:
                if (isCorner(s)) {
                    if (!s.withForce(1).tryExpand() &&
                            !s.beBrave().cutCorners().withForce(2).tryExpand()) {
                        s.beBrave().cutCorners().withForce(2).tryCollapse();
                    }
                } else {
                    if (s.in.matchesAny("""
                            x  |  x
                              x|x \s
                            """)) {
                        if (!s.tryExpand() &&
                                !s.withForce(1).tryExpand() &&
                                !s.beBrave().cutCorners().withForce(3).tryExpand()) {
                            s.withForce(1).tryCollapse();
                        }
                    } else {
                        if (!s.tryCollapse() &&
                                !s.withForce(1).tryExpand() &&
                                !s.withForce(2).tryExpand() &&
                                !s.withForce(1).tryCollapse())
                            s.beBrave().cutCorners().withForce(3).tryExpand();
                    }
                }
//                if (s.in.matchesAny("""
//                          x|   |x  |x x
//                          x|x x|x  |  \s
//                        """)) {
//                if (!s.tryExpand() && !s.tryCollapse()) {
//                    if (!s.withForce(1).tryExpand() && !s.withForce(1).tryCollapse()) {
//                            if (!s.withForce(1).cutCorners().tryExpand() && !s.withForce(1).cutCorners().tryCollapse()) {
//                        if (!s.withForce(2).tryExpand()) {
//                                    s.withForce(2).cutCorners().tryExpand();
//                        }
//                            }
//                    }
//                        s.findInMatchingRotation("""
//                                  \s
//                                x x
//                                """, s1 -> {
//                            if (s1.withForce(1).move(0, -1).tryMegaCollapse()) {
//                                // TODO need to make it in one transaction?
//                                s1.tryExpand();
//                            } else {
//                                s1.withForce(1).cutCorners().tryCollapse();
//                            }
//                        });
//                }
//                } else if (s.in.matchesAny("""
//                        x  |x x
//                        x  |  \s
//                        """)) {
//                    if (!s.tryExpand() && !s.tryCollapse()) {
//                        if (!s.withForce(1).cutCorners().tryExpand()) s.withForce(1).cutCorners().tryCollapse();
//                    }
//                } else {
//                    s.tryExpand();
//                }
                break;
            case 3:
                if (!s.tryExpand()) {
                    if (isHole(s)) {
                        s.withForce(1).tryCollapse();
                    } else if (isCorner(s)) {
                        if (!s.withForce(1).tryExpand()) {
                            s.withForce(2).beBrave().cutCorners().tryExpand();
                        }
                    } else {
                        if (!s.withForce(1).tryExpand() && !s.withForce(1).tryCollapse()) {
                            s.withForce(2).cutCorners().tryExpand();
                        }
                    }
                }
                break;
            case 4:
                s.out.set(0, 0, BLACK);
                break;
        }
    }

    protected void evolveCorners(Strider s) {
        if (isCorner(s)) {
            log(s.x + ", " + s.y);
            log(cipher);
            s.tryExpand();
        }
    }

    private static boolean isCorner(Strider s) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                if (s.in.get(dx, dy) == BLACK) {
                    Fragment square = new Fragment(s.in, dx - 1, dy - 1, 3, 3);
                    if (isCorner(square)) return true;
                }
            }
        }
        return false;
    }

    private static boolean isHole(Strider s) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                if (s.in.get(dx, dy) == WHITE) {
                    Fragment square = new Fragment(s.in, dx - 1, dy - 1, 3, 3);
                    if (isHole(square)) return true;
                }
            }
        }
        return false;
    }

    private static boolean isCorner(Fragment square) {
        switch (square.count()) {
            case 2 -> {
                return true;
            }
            case 3 -> {
                return !square.isBridge();
            }
        }
        return false;
    }

    private static boolean isCornerCorner(Fragment square) {
        switch (square.count()) {
            case 2 -> {
                return true;
            }
            case 3 -> {
                if (!square.isBridge()) {
                    AtomicBoolean exception = new AtomicBoolean();
                    square.findMatchingRotation("""
                                \s
                              x .
                              . x
                            """, r -> {
                        Matrix sq2 = r.t.mute(square);
                        sq2.findMatchingSymmetry("""
                                            \s
                                          x \s
                                          x x
                                        """,
                                Symmetry.TRANSPOSE,
                                s -> {
                                    if (s.t.mute(sq2).matches("""
                                                  .
                                              x   .
                                              x x x
                                            """)) {
                                        exception.set(true);
                                    }
                                });
                    });
                    return !exception.get();
                }
            }
        }
        return false;
    }

    private static boolean isHole(Fragment square) {
        return square.matches("""
                . x .
                x   x
                . x .
                """);
    }

    protected void forAllCells(Consumer<Strider> consumer) {
        for (int y = 0; y < cipher.getHeight() / 2; y++) {
            for (int x = 0; x < cipher.getWidth() / 2; x++) {
                consumer.accept(new Strider(x, y));
            }
        }
    }

    void log(Object log) {
//        if (n == 3) System.out.println(log);
    }

    public class Strider implements Cloneable {
        Matrix in;
        Matrix out;
        private int force;
        private boolean cutCorners;
        final int x;
        final int y;

        private int cx;
        private int cy;

        public Strider(int x, int y) {
            this.in = new Fragment(cipher, x * 2, y * 2, 2, 2);
            this.out = new Fragment(cout, x, y, 1, 1);
            this.x = x;
            this.y = y;
        }

        @Override
        public Strider clone() {
            try {
                return (Strider) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean findInMatchingRotation(String pattern, Consumer<Uncaptcha.Compact3.Strider> action) {
            return in.findMatchingRotation(pattern, r -> action.accept(transform(r.t)));
        }

        public void forEachInMatchingRotation(String pattern, Consumer<Uncaptcha.Compact3.Strider> action) {
            for (Rotation r : Rotation.values()) {
                Matrix apply = r.t.mute(in);
                if (apply.matches(pattern)) {
                    action.accept(transform(r.t));
                }
            }
        }

        public void forAllSymmetries(Symmetry symmetry, Consumer<Uncaptcha.Compact3.Strider> action) {
            action.accept(this);
            action.accept(transform(symmetry.t));
        }

        private Strider transform(Transmutation t) {
            Strider clone = clone();
            clone.in = t.mute(in);
            clone.out = t.mute(out);
            clone.cx = 0;
            clone.cy = 0;
            return clone;
        }

        private Strider transformCorner(Transmutation t) {
            Strider clone = clone();
            clone.in = t.mute(in);
            clone.out = t.mute(out);
            clone.cx = t.back().transmuteX(cx, cy, in);
            clone.cy = t.back().transmuteY(cx, cy, in);
//            log(new Fragment(in, -1, -1, 4, 4));
//            log(cx+"x"+cy);
//            log(new Fragment(clone.in, -1, -1, 4, 4));
//            log(clone.cx+"x"+clone.cy);
            return clone;
        }

        public boolean change(int dx, int dy, String from, String to) {
            return move(dx, dy).ifOutMatches(from, s -> s.fit(to));
        }

        public void change(Symmetry symmetry, int dx, int dy, String from, String to) {
            forAllSymmetries(symmetry, s -> s.change(dx, dy, from, to));
        }

        public Uncaptcha.Compact3.Strider move(int dx, int dy) {
            Strider clone = clone();
            clone.in = new Fragment(in, dx * 2, dy * 2, 2, 2);
            clone.out = new Fragment(out, dx, dy, 1, 1);
            return clone;
        }

        public boolean ifOutMatches(String pattern, Consumer<Uncaptcha.Compact3.Strider> action) {
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
                if (template.get(x, y) == Uncaptcha.WHITE) {
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
            String pave = path("-");
            try {
                return tryIt(() -> collapse());
            } finally {
                path = pave;
            }
        }

        private boolean tryIt(Runnable action) {
            String cave = cipher.toString();
            String oave = cout.toString();
            try {
                action.run();
                return true;
            } catch (RuntimeException e) {
                cipher.set(cave);
                cout.set(oave);
                log("NO: " + e.getMessage() + "\n");
                return false;
            }
        }

        public void log(Object o) {
            if (path.startsWith("2,2"))
                SuperCompact.this.log(o);
        }

        public boolean tryExpand() {
            String pave = path("+");
            String cave = cipher.toString();
            String oave = cout.toString();
            try {
                expand();
                return true;
            } catch (RuntimeException e) {
                cipher.set(cave);
                cout.set(oave);
                return false;
            } finally {
                path = pave;
            }
        }

        private String path(String op) {
            String pave = path;
            if ("".equals(path)) {
                path = x + "," + y + op + force;
                if (bold) {
                    path += "#";
                } else if (cutCorners) {
                    path += "!";
                }
            }
            return pave;
        }

        public void collapse() {
            if (in.count() != 2 || !findInMatchingRotation("""
                      x
                    x \s
                    """, s -> {
                Stream.of(Rotation.ZERO, Rotation.AROUND).anyMatch(r -> Stream.of(Symmetry.NONE, Symmetry.TRANSPOSE).anyMatch(m -> {
                    Strider s1 = s.transform(r.t).transform(m.t);
                    if (s1.in.get(0, 2) == WHITE &&
                            s1.corner(0, 1, force + 1).tryCollapseCorner()) {
                        return tryCollapse();
                    }
                    return false;
                }));
            })) {
                findInMatchingRotation("""
                        x
                        \s
                        """, s1 -> {
                    s1.forEachInMatchingRotation("""
                            x
                            \s
                            """, Strider::tryCollapseCorner);
                });

                transform(Symmetry.TRANSPOSE.t).findInMatchingRotation("""
                        x
                        \s
                        """, s1 -> {
                    s1.forEachInMatchingRotation("""
                            x
                            \s
                            """, Strider::tryCollapseCorner);
                });
            }

            if (in.count() != 0) throw new RuntimeException("Couldn't");
            out.set(0, 0, Uncaptcha.WHITE);
        }

        private boolean bold;

        public void collapseCorner() {
            if (out.get(0, 0) == BLACK) {
                if (!bold) throw new RuntimeException("Timid");
            }
            Fragment up = new Fragment(in, -1 + cx, -1 + cy, 3, 3);
            log(path);
            log("Collapse before " + force);
            log(new Fragment(up, -1, -1, 5, 5));

            if (isCornerCorner(up)) {
                try {
                    corner(0, -1).expandCorner();
                } catch (RuntimeException e) {
                    if (!cutCorners) throw new RuntimeException("Corner", e);
                }
//                throw new RuntimeException("Corner");
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
//            if (up.matchesAny("""
//                    .   x
//                    . x \s
//                    . . .
//                    """)) {
//                corner(1, 0).expandCorner();
//            }
//            if (up.matchesAny("""
//                    x   .
//                      x .
//                    . . .
//                    """)) {
//                corner(-1, 0).expandCorner();
//            }
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
            this.in.set(cx, cy, Uncaptcha.WHITE);
            log("Collapse after");
            log(cipher);
            if (out.get(0, 0) == BLACK) collapse();
        }

        private Strider zeroCorner() {
            Strider clone = clone();
            clone.cx = 0;
            clone.cy = 0;
            return clone;
        }

        private boolean tryCollapseCorner() {
            String cave = cipher.toString();
            String oave = cout.toString();
            try {
                collapseCorner();
                return true;
            } catch (RuntimeException e) {
                cipher.set(cave);
                cout.set(oave);
                log("NO: " + e.getMessage() + "\n");
                return false;
            }
        }


        public boolean tryMegaCollapse() {
            String cave = cipher.toString();
            String oave = cout.toString();
            try {
                megaCollapse();
                return true;
            } catch (RuntimeException e) {
                cipher.set(cave);
                cout.set(oave);
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
            this.out.set(0, 0, Uncaptcha.WHITE);
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
            return corner(dx, dy, force);
        }

        private Uncaptcha.Compact3.Strider corner(int dx, int dy, int force) {
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

                Strider clone = clone();
                clone.in = new Fragment(in, bx * 2, by * 2, 2, 2);
                clone.out = new Fragment(out, bx, by, 1, 1);
                clone.force = force - 1;
                clone.cx = dx;
                clone.cy = dy;
                return clone;
            }
        }

        String path = "";

        public void orderExpand() {
            cx = 0;
            cy = 0;
            log("orderExpand");
            if (in.matches("""
                    . .
                    x \s
                    """)) {
                corner(1, 1).transformCorner(Transmutation.ROTATE_LEFT).expandCorner();
            } else if (in.matches("""
                    . .
                      x
                    """)){
                corner(0, 1).transformCorner(Transmutation.ROTATE_RIGHT).expandCorner();
            }
            for (int dx = 0; dx < 2; dx++) {
                for (int dy = 1; dy >= 0; dy--) {
                    if (in.get(dx, dy) == WHITE) {
                        corner(dx, dy).expandCorner();
                    }
                }
            }
            out.set(0, 0, BLACK);
            log("after orderExpand");
        }

        public void expand() {
            switch (in.count()) {
                case 1:
                    findInMatchingRotation("""
                              \s
                              x
                            """, s1 -> {
                        int left = new Fragment(s1.in, -1, 0, 3, 3).count();
                        int right = new Fragment(s1.in, 0, -1, 3, 3).count();
                        if (left >= right) {
                            s1.transform(Rotation.RIGHT.t).expandCorner();
                            expand();
                        } else {
                            s1.transform(Symmetry.X.t).expandCorner();
                            expand();
                        }
                    });
                    break;
                case 2:
                    findInMatchingRotation("""
                              \s
                            x x
                            """, s1 -> {
                        if (!s1.tryExpandCorner() || !s1.transform(Symmetry.X.t).tryExpandCorner()) {
                            s1.transform(Symmetry.X.t).expandCorner();
                            s1.tryExpandCorner();
                        }
                    });
                    findInMatchingRotation("""
                              x
                            x \s
                            """, s1 -> {
                        if (!s1.tryExpandCorner()) s1.transform(Symmetry.TRANSPOSE.t).expandCorner();
                        expand();
                    });
                    break;
                case 3:
                    findInMatchingRotation("""
                              x
                            x x
                            """, s1 -> {
                        if (!s1.tryExpandCorner()) s1.transform(Symmetry.TRANSPOSE.t).expandCorner();
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
            String cave = cipher.toString();
            String oave = cout.toString();
            try {
                expandCorner();
                return true;
            } catch (RuntimeException e) {
                cipher.set(cave);
                cout.set(oave);
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
            try {
                Fragment up = new Fragment(in, -1 + cx, -1 + cy, 3, 3);
                log(path);
                log("Expand Before " + force);
                log(new Fragment(up, -1, -1, 5, 5));
//                if (!cutCorners)
                    if (up.matches("""
                            . x .
                            .   .
                            . x .
                            """)) {
                        if (up.matches("""
                                . x .
                                    x
                                . x .
                                """) && !up.matches("""
                                x x .
                                    x
                                x x .
                                """)) {
                            transformCorner(Transmutation.ROTATE_RIGHT).tryExpandCorner();
                        } else if (up.matches("""
                                . x .
                                x   \s
                                . x .
                                """) && !up.matches("""
                                . x x
                                x   \s
                                . x x
                                """)) {
                            transformCorner(Transmutation.ROTATE_LEFT).tryExpandCorner();
                        } else {
                            corner(0, -1).collapseCorner();
                        }
                    } else {
                        if (new Fragment(up, 0, -1, 3, 3).matches("""
                            . x .
                            .   .
                            .   .
                            """)) {
                            corner(0, -2).collapseCorner();
                        }
                    }
//                    if (up.matchesAny("""
//                            . x x|x x .
//                            x    |    x
//                            . x x|x x .
//                            """
//                    )) {
//                        corner(0, -1).collapseCorner();
////                throw new RuntimeException("Corner");
//                    }
                if (new Fragment(up, 0, -1, 3, 3).matchesAny("""
                        . x .
                        x   x
                        .   .
                        """)) {
                    this.in.set(cx, cy - 1, BLACK);
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
                    if (cutCorners && new Move(-1, 0).mute(up).matches("""
                            . x \s
                            x   \s
                            . x x
                            """)) {
                        corner(-1, 0).expandCorner();
                    } else {
                        corner(-1, -1).collapseCorner();
                    }
                }
                if (new Fragment(up, -1, 1, 3, 2).matches("""
                        x   \s
                        x   x
                        . x .
                        """)) {
                    corner(-1, 1).expandCorner();
                }
                if (new Fragment(up, 1, 1, 3, 2).matches("""
                        x   \s
                        x   x
                        . x .
                        """)) {
                    corner(1, 1).expandCorner();
                }
                this.in.set(cx, cy, BLACK);
                log("Expand After");
                log(cipher);
                if (out.get(0, 0) == Uncaptcha.WHITE) {
                    out.set(0, 0, GRAY);
//                    withForce(force + 1).orderExpand();
                }
            } catch (RuntimeException e) {
                log("NO: " + e.getMessage() + "\n");
                throw e;
            }
        }

        public Uncaptcha.Compact3.Strider withForce(int force) {
            Strider clone = clone();
            clone.force = force;
            return clone;
        }

        public Uncaptcha.Compact3.Strider cutCorners() {
            Strider clone = clone();
            clone.cutCorners = true;
            return clone;
        }

        public Uncaptcha.Compact3.Strider beBrave() {
            Strider clone = clone();
            clone.bold = true;
            return clone;
        }

    }
}
