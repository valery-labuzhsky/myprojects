package tools.bindiff.compare;

/**
 * Created by ptasha on 14/02/15.
 */
public class Line implements Comparable<Line> {
    private final int index1;
    private final int index2;

    public Line(int index1, int size) {
        this.index1 = index1;
        this.index2 = index1 + size - 1;
    }

    public boolean intersect(Line line) {
        if (index1 > line.index1) {
            return index2 <= line.index1;
        } else {
            return index1 >= line.index2;
        }
    }

    public int getIndex1() {
        return index1;
    }

    public int getIndex2() {
        return index2;
    }

    @Override
    public String toString() {
        return String.format("[%6d: %6d, %6d]", size(), index1, index2);
    }

    public int size() {
        return index2 - index1 + 1;
    }

    public boolean contains(int index1, int index2) {
        return this.index1 <= index1 && index2 <= this.index2;
    }

    @Override
    public int compareTo(Line o) {
        return index1 - o.index1;
    }

    public boolean contains(Line line) {
        return contains(line.index1, line.index2);
    }

    public Line merge(Line line) {
        int index1 = Math.min(this.index1, line.index1);
        int index2 = Math.max(this.index2, line.index2);
        return new Line(index1, index2 - index1 + 1);
    }
}
