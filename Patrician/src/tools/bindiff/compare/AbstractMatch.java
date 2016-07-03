package tools.bindiff.compare;

import java.util.Comparator;

/**
 * Created by ptasha on 17/02/15.
 */
public abstract class AbstractMatch implements Comparable<AbstractMatch> {
    public abstract int size();
    public abstract int getIndex(int n);

    @Override
    public int compareTo(AbstractMatch o) {
        int size = o.size() - size();
        if (size!=0) {
            return size;
        }
        int delta = getDelta() - o.getDelta();
        if (delta!=0) {
            return delta;
        }
        int sum = getSum() - o.getSum();
        if (sum!=0) {
            return sum;
        }
        return getIndex(0) - o.getIndex(0);
    }

    private int getDelta() {
        return Math.abs(getIndex(0) - getIndex(1));
    }

    private int getSum() {
        return getIndex(0) + getIndex(1);
    }

    public boolean intersects(AbstractMatch match) {
        if (match==null) return false;
        if (intersect(match, 0)) return true;
        if (intersect(match, 1)) return true;
        return false;
    }

    private boolean intersect(AbstractMatch match, int n) {
        if (getIndex(n) > match.getIndex(n)) {
            return getIndex(n) <= match.getIndex2(n);
        } else {
            return getIndex2(n) >= match.getIndex(n);
        }
    }

    public int getIndex2(int n) {
        return getIndex(n) + size() - 1;
    }

    public int getDiff() {
        return getIndex(0) - getIndex(1);
    }

    public static class LineComparator implements Comparator<AbstractMatch> {
        private final int n;

        public LineComparator(int n) {
            this.n = n;
        }

        @Override
        public int compare(AbstractMatch o1, AbstractMatch o2) {
            return o1.getIndex(n) - o2.getIndex(n);
        }
    }
}
