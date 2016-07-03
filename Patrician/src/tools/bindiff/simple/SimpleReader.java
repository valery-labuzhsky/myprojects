package tools.bindiff.simple;

import patrician.Paths;
import patrician.save.SaveReader;

import java.io.*;

/**
 * Created by ptasha on 14/02/15.
 */
public class SimpleReader {
    public static void main(String[] args) throws IOException {
        // 41  0  | ff ff 57  0  | 55  0 90 de  |  1  0 c7 ec  | 17  6 57 4a  |  0  3 e8 9e | c1  3 26 f2  | ff ff 72  1 |  |  1  0 4b  0  |  0  0  5  0  |  1  f 12 ff  | ff ff  0  0  | f6  6 2d  0  | 18  0 15  4  | 40  7 48  7  | 40  7  0 bf
        Sequence base = SaveReader.getSequence(Paths.getGame("base2"));
        Sequence base2 = SaveReader.getSequence(Paths.getGame("base"));
        base.skip(930998 - 120 + 26);
        base2.skip(930998 - 120);
        compare(base, base2);
    }

    public static void compare(File file1, File file2) throws IOException {
        compare(SaveReader.getSequence(file1), SaveReader.getSequence(file2));
    }

    private static Sequence getSequence(File file) throws FileNotFoundException {
        return getSequence(new FileInputStream(file));
    }

    private static Sequence getSequence(InputStream stream) {
        return new InputStreamSequence(stream);
    }

    public static void compare(Sequence sequence1, Sequence sequence2) throws IOException {
        BinCompare diff = new DiffsStrings();
        while (!diff.isFull()) {
            Integer b1 = null;
            if (sequence1.hasNext()) {
                b1 = sequence1.next();
            }
            Integer b2 = null;
            if (sequence2.hasNext()) {
                b2 = sequence2.next();
            }
            diff.write(b1, b2);
        }
        sequence1.close();
        sequence2.close();
        diff.print();
    }

    public static void compare(Sequence sequence1, Sequence sequence2, int offset1, int offset2) throws IOException {
        skip(sequence1, sequence2, offset1, offset2);
        compare(sequence1, sequence2);
    }

    public static void skip(Sequence sequence1, Sequence sequence2, int offset1, int offset2) {
        sequence1.skip(offset1);
        sequence2.skip(offset2);
    }
}
