package tools.bindiff.findchange;

import patrician.Paths;
import patrician.save.SaveReader;
import tools.bindiff.simple.Sequence;

import java.io.IOException;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class SequenceChange {
    public static void main(String[] args) throws IOException {
//        System.out.println(next(0xff000000, 0));
        int before = 2;
        int after = 1;
        Sequence input1 = SaveReader.getSequence(Paths.getGame("base"));
        Sequence input2 = SaveReader.getSequence(Paths.getGame("base2"));
        int i=-3;
        int i1 = 0;
        int i2 = 0;
        while (input1.hasNext() && input2.hasNext()) {
            i1 = next(i1, input1.next());
            i2 = next(i2, input2.next());
            if (i1*after == i2*before && i1!=i2) {
//            if (i1!=i2) {
                System.out.println(String.format("%11d: %11d -> %11d", i, i1, i2));
            }
            i++;
        }
        input1.close();
        input2.close();
    }

    private static int next(int i, int next) {
        i >>= 8;
        i &= 0xffffff;
        i |= next << (8 * 3);
        return i;
    }
}
