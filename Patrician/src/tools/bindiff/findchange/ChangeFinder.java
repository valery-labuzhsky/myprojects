package tools.bindiff.findchange;

import tools.bindiff.compare.ByteArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created on 22/02/15.
 *
 * @author ptasha
 */
public class ChangeFinder {
    public static void main(String[] args) throws IOException {
        String folder = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/AutoRoute/";
//        String folder = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/Kam/";
        ByteArray array1 = new ByteArray(folder + "test.rou");
        ByteArray array2 = new ByteArray(folder + "test2.rou");
//        ByteArray array1 = new ByteArray(folder + "base2.pat");
//        ByteArray array2 = new ByteArray(folder + "honey.pat");
        ArrayList<Integer> before = find(array1, 130);
        ArrayList<Integer> after = find(array2, 50);
        TreeSet<Pair> set = new TreeSet<>();
        for (Integer index1 : before) {
            for (Integer index2 : after) {
                Pair pair = new Pair(index1, index2);
                if (pair.matches(array1, array2)) {
                    set.add(pair);
                }
            }
        }
        System.out.println(set);
        ByteChanger changer = new ByteChanger(folder + "test2.rou", folder + "test3.rou");
        for (Pair pair : set) {
            changer.change(pair.after, 130);
        }
//        changer.change(28026, 205);
        changer.change();
    }

    private static ArrayList<Integer> find(ByteArray array, int c) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i=0; i<array.size(); i++) {
            if (array.get(i)==(byte)c) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    public static class Pair implements Comparable<Pair> {
        private final int before;
        private final int after;

        public Pair(int before, int after) {
            this.before = before;
            this.after = after;
        }

        @Override
        public int compareTo(Pair o) {
            int delta = this.getDelta() - o.getDelta();
            if (delta!=0) {
                return delta;
            }
            int sum = this.getSum() - o.getSum();
            if (sum!=0) {
                return sum;
            }
            return before - o.before;
        }

        private int getDelta() {
            return Math.abs(before - after);
        }

        private int getSum() {
            return before + after;
        }

        @Override
        public String toString() {
            return String.format("%d->%d", before, after);
        }

        public boolean matches(ByteArray array1, ByteArray array2) {
            for (int i=1; i<=1; i++) {
                if (array1.get(before-i) != array2.get(after - i)) {
                    return false;
                }
            }
            for (int i=1; i<=4; i++) {
                if (array1.get(before+i) != array2.get(after + i)) {
                    return false;
                }
            }
            return true;
        }
    }
}
