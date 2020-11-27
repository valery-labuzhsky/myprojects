package board;

import java.util.function.Predicate;

/**
 * Created on 01.05.2020.
 *
 * @author ptasha
 */
public class XY {
    public int x;
    public int y;

    public XY() {
    }

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public XY(Pair pair) {
        set(pair);
    }

    public XY set(Pair pair) {
        x = pair.file;
        y = pair.rank;
        return this;
    }

    public void minus(Pair pair) {
        x -= pair.file;
        y -= pair.rank;
    }

    public void plus(Pair pair) {
        x += pair.file;
        y += pair.rank;
    }

    public boolean linear() {
        return true;
    }

    public boolean diagonal() {
        int x0 = x;
        x = x + y;
        y = x0 - y;
        return true;
    }

    public boolean diagonalBack() {
        if (x % 2 != y % 2) {
            return false;
        }
        int x0 = x;
        x = (x + y) / 2;
        y = (x0 - y) / 2;
        return true;
    }

    public boolean almondXPlus() {
        y = y + x;
        return true;
    }

    public boolean almondXMinus() {
        y = y - x;
        return true;
    }

    public boolean almondYPlus() {
        x = x + y;
        return true;
    }

    public boolean almondYMinus() {
        x = x - y;
        return true;
    }

    public boolean transpose() {
        int y = this.y;
        this.y = x;
        x = y;
        return true;
    }

    public boolean xmirror() {
        x = -x;
        return true;
    }

    public void swap(XY xy) {
        int y = this.y;
        this.y = xy.y;
        xy.y = y;
    }

    public void swap() {
        int t = this.x;
        this.x = this.y;
        this.y = t;
    }

    @Override
    public String toString() {
        return x + "x" + y;
    }

    public static class Transform {
        public static final Transform LINEAR = new Transform(XY::linear, XY::linear);
        public static final Transform DIAGONAL = new Transform(XY::diagonal, XY::diagonalBack);
        public static final Transform ALMOND_X_PLUS = new Transform(XY::almondXPlus, XY::almondXMinus);
        public static final Transform ALMOND_X_MINUS = new Transform(XY::almondXMinus, XY::almondXPlus);
        public static final Transform ALMOND_Y_PLUS = new Transform(XY::almondYPlus, XY::almondYMinus);
        public static final Transform ALMOND_Y_MINUS = new Transform(XY::almondYMinus, XY::almondYPlus);
        public static final Transform TRANSPOSE = new Transform(XY::transpose, XY::transpose);
        public static final Transform XMIRROR = new Transform(XY::xmirror, XY::xmirror);

        private final Predicate<XY> forth;
        private final Predicate<XY> back;

        Transform(Predicate<XY> forth, Predicate<XY> back) {
            this.forth = forth;
            this.back = back;
        }

        public boolean transform(XY xy) {
            return forth.test(xy);
        }

        public boolean back(XY xy) {
            return back.test(xy);
        }

        public Transform combine(Transform trasform) {
            return new Transform(
                    xy -> forth.test(xy) && trasform.forth.test(xy),
                    xy -> trasform.back.test(xy) && back.test(xy));
        }

        public Transform combine(Transform transform, XY from) {
            transform.transform(from);
            return combine(transform);
        }

        public static Transform shift(Pair pair) {
            return new Transform(xy -> {
                xy.x -= pair.file;
                xy.y -= pair.rank;
                return true;
            }, xy -> {
                xy.x += pair.file;
                xy.y += pair.rank;
                return true;
            });
        }

        public static Transform normal(XY norm, Square from, Square to) {
            norm.set(to.pair);
            Transform t = shift(from.pair);
            t.transform(norm);
            if (norm.x == 0) {
                t = t.combine(DIAGONAL, norm);
            } else if (norm.x == norm.y) {
                t = t.combine(ALMOND_X_MINUS, norm);
            } else if (norm.x == -norm.y) {
                t = t.combine(ALMOND_X_PLUS, norm);
            } else {
                return null;
            }
            if (norm.x < 0) {
                t = t.combine(XMIRROR, norm);
            }
            return t;
        }

    }
}
