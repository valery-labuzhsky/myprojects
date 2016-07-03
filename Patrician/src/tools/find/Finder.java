package tools.find;

import patrician.Paths;
import patrician.save.SaveReader;
import patrician.save.objects.RoutePoint;
import patrician.save.objects.Ship;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Created on 09/03/15.
 *
 * @author ptasha
 */
public class Finder {
    private final List<Integer> data;

    private final int[] needle;
    private final HashMap<Integer, TreeSet<Integer>> hash;
    private int best;
    private int pointer;

    public Finder(List<Integer> data, int[] needle) {
        this.data = data;
        this.needle = needle;
        hash = new HashMap<>();
        for (int i= needle.length-1; i>=0; i--) {
            int b = needle[i];
            TreeSet<Integer> indexes = hash.get(b);
            if (indexes==null) {
                indexes = new TreeSet<>();
                hash.put(b, indexes);
            }
            indexes.add(i);
        }
        best = 0;
        for (int i = 0; i < needle.length; i++) {
            if (hash.get(needle[i]).size() <= hash.get(needle[best]).size()) {
                best = i;
            }
        }
    }

    public Finder(List<Integer> data, String string) {
        this(data, parseBytes(string));
    }

    public static int[] parseBytes(String string) {
        String[] bytes = string.split("[ |]+");
        int[] needle = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            needle[i] = Integer.parseInt(bytes[i], 16);
        }
        return needle;
    }

    public int next() {
        while (pointer <data.size() - needle.length) {
            int n;
            for (n = best; n>=0; n--) {
                if (data.get(pointer +n)!=needle[n]) {
                    break;
                }
            }
            if (n==-1) {
                for (n = needle.length-1; n>best; n--) {
                    if (data.get(pointer +n)!=needle[n]) {
                        break;
                    }
                }
                if (n==best) {
                    n = -1;
                }
            }
            if (n==-1) {
                return pointer++;
            } else {
                Integer lower = null;
                TreeSet<Integer> indexes = hash.get(data.get(pointer + n));
                if (indexes!=null) {
                    lower = indexes.lower(n);
                }
                if (lower == null) {
                    pointer += n + 1;
                } else {
                    pointer += n - lower;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
//        int [] bytes = RoutePoint.ROUTE;
        int[] bytes = parseBytes("0 ff  0 8f |  0"); // 908688
//        int[] bytes = getBytes("Ptasha");
//        int[] bytes = Ship.SHIPS; // 908596 + 100 = 908696
        Finder finder = new Finder(SaveReader.getData(Paths.getGame("base2")), bytes);
        int pointer;
        int counter = 0;
        while ((pointer=finder.next())>=0) {
            System.out.println(pointer);
            counter++;
        }
        System.out.println(counter);
        // 961129 960040
    }

    private static int[] getBytes(String name) throws UnsupportedEncodingException {
        byte[] raw = name.getBytes("cp1251");
        int[] bytes = new int[raw.length];
        for (int i = 0; i < raw.length; i++) {
            int b = raw[i];
            if (b<0) {
                b &= 0xff;
            }
            bytes[i] = b;
        }
        return bytes;
    }
}
