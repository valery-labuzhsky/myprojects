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

    public void set(Pair pair) {
        x = pair.file;
        y = pair.rank;
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

    public enum Transform {
        LINEAR(XY::linear, XY::linear),
        DIAGONAL(XY::diagonal, XY::diagonalBack),
        ALMOND_X_PLUS(XY::almondXPlus, XY::almondXMinus),
        ALMOND_X_MINUS(XY::almondXMinus, XY::almondXPlus),
        ALMOND_Y_PLUS(XY::almondYPlus, XY::almondYMinus),
        ALMOND_Y_MINUS(XY::almondYMinus, XY::almondYPlus);

        private final Predicate<XY> transform;
        private final Predicate<XY> back;

        Transform(Predicate<XY> transform, Predicate<XY> back) {
            this.transform = transform;
            this.back = back;
        }

        public boolean transform(XY xy) {
            return transform.test(xy);
        }

        public boolean back(XY xy) {
            return back.test(xy);
        }
    }
}
