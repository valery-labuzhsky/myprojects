package tools.bindiff.compare;

import java.io.IOException;
import java.util.*;

/**
 * Created by ptasha on 14/02/15.
 */
public class Reader {
    public static void main(String[] args) throws IOException {
        String folder = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/Kam/";
        ByteArray array1 = new ByteArray(folder + "base2.pat");
        ByteArray array2 = new ByteArray(folder + "honey.pat");
        Hash hash1 = new Hash(array1, 0);
        Hash hash2 = new Hash(array2, 1);
        hash1.intersect(hash2);
        while (!hash1.isEmpty()) {
            System.out.println(hash1);
            hash1 = new Hash(hash1);
            hash2 = new Hash(hash2);
            hash1.intersect(hash2);
        }
        hash1 = hash1.getParent();
        hash2 = hash2.getParent();
        Collection<? extends AbstractMatch> matches = hash1.match(hash2);
        print(hash1, hash2, matches, 205, 200);
//        print(matches);
//        printSizes(matches);
    }

    private static void print(Hash hash1, Hash hash2, Collection<? extends AbstractMatch> matches, int before, int after) {
        for (AbstractMatch match : matches) {
            int diff = match.getDiff();
            int index = match.getIndex(0);
            for (int i=0; i<match.size(); i++) {
                int b = hash1.get(index+i);
                int a = hash2.get(index+i-diff);
                if (a==after && b==before) {
                    System.out.println(match);
                }
            }
            /*if (match.size() == 1 && hash1.get(match.getIndex(0)) == before && hash2.get(match.getIndex(1)) == after) {
                System.out.println(match);
            }*/
            /*if (match.size() == 1) {
                System.out.println(hash1.get(match.getIndex(0))+"->"+hash2.get(match.getIndex(1)));
            }*/
        }
    }

    private static void printSizes(Collection<AbstractMatch> matches) {
        TreeMap<Integer, Integer> sizes = new TreeMap<>();
        for (AbstractMatch match : matches) {
            Integer count = sizes.get(match.size());
            if (count==null) {
                count = 0;
            }
            count++;
            sizes.put(match.size(), count);
        }
        for (Map.Entry<Integer, Integer> entry : sizes.entrySet()) {
            System.out.println(entry);
        }
    }

    private static void print(Collection<? extends AbstractMatch> matches) {
        System.out.println("Size: "+matches.size());
        for (AbstractMatch match : matches) {
            System.out.println(match);
        }
    }
}
