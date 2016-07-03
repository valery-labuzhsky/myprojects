package tools.bindiff.compare;

/**
 * Created by ptasha on 14/02/15.
 */
public class Match extends AbstractMatch {
    private final Line[] lines = new Line[2];

    public Match(int size, int... indexes) {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(indexes[i], size);
        }
    }

    @Override
    public String toString() {
        return lines[0] +"->"+ lines[1];
    }

    @Override
    public int size() {
        return lines[0].size();
    }

    public Line getLine(int n) {
        return lines[n];
    }

    @Override
    public int getIndex(int n) {
        return lines[n].getIndex1();
    }

    public static Match extend(int size, int[] indexes, ByteArray[] arrays) {
        int index1 = indexes[0];
        int index2 = indexes[1];
        while (arrays[0].exists(index1 + size) && arrays[1].exists(index2 + size) && (arrays[0].get(index1 + size)== arrays[1].get(index2 + size))) {
            size++;
        }
        while (arrays[0].exists(index1 - 1) && arrays[1].exists(index2 - 1) && (arrays[0].get(index1 - 1)== arrays[1].get(index2 - 1))) {
            index1--;
            index2--;
            size++;
        }
        return new Match(size, index1, index2);
    }

    public Match merge(Match match) {
        int index1 = Math.min(getIndex(0), match.getIndex(0));
        int index2 = Math.max(getIndex2(0), match.getIndex2(0));
        int diff = getIndex(0) - getIndex(1);
        return new Match(index2 - index1 + 1, index1, index1 - diff);
    }

    public Match subtract(AbstractMatch match, int n) {
        if (match==null) {
            return this;
        }
        if (getIndex(n) > match.getIndex(n)) {
            int up = match.getIndex2(n) - getIndex(n) + 1;
            if (up > 0) {
                if (up >= size()) {
                    return null;
                }
                int[] indexes = new int[2];
                indexes[n] = getIndex(n) + up;
                indexes[1 - n] = getIndex(1 - n) + up;
                return new Match(size() - up, indexes);
            }
        } else {
            int down = getIndex2(n) - match.getIndex(n) + 1;
            if (down > 0) {
                if (down >= size()) {
                    return null;
                }
                return new Match(size() - down, getIndex(0), getIndex(1));
            }
        }
        return this;
    }

    public static void main(String[] args) {
        System.out.println(new Match(10, 10, 10).subtract(new Match(10, 2, 10), 1));
    }
}
