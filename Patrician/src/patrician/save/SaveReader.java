package patrician.save;

import patrician.Paths;
import tools.bindiff.simple.IntSequence;
import tools.bindiff.simple.Sequence;
import tools.bindiff.simple.SimpleReader;
import tools.bindiff.simple.StringSettings;

import java.io.*;
import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class SaveReader {
    private static final String ROUTES = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/AutoRoute/";

    private final InputStream input;
    private final Decompressor decompressor = new Decompressor();

    public SaveReader(String route) throws IOException {
        this(Paths.getRoute(route));
    }

    public SaveReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public SaveReader(InputStream input) throws IOException {
        this.input = input;
        readSave();
    }

    private static final int SKIP_BYTES = 0;

    private void readSave() throws IOException {
        if (input.skip(SKIP_BYTES)!= SKIP_BYTES) {
            input.close();
            throw new IOException("Bad header: not enough bytes to skip = "+ SKIP_BYTES);
        }
        try {
            int b;
            while ((b = input.read()) != -1) {
                decompressor.writeByte(b);
            }
        } finally {
            input.close();
        }
    }

    public static void main(String[] args) throws IOException {
        StringSettings.setInt();
        Sequence input1 = getIntSequence(Paths.getGame("base"));
        Sequence input2 = getIntSequence(Paths.getGame("base2"));
        SimpleReader.compare(input1, input2);
    }

    public static IntSequence getIntSequence(File file1, int skip) throws IOException {
        return new IntSequence(getSequence(file1), skip);
    }

    public static DataSequence getSequence(File file1) throws IOException {
        return new DataSequence(getData(file1));
    }

    public static List<Integer> getData(File file1) throws IOException {
        return new SaveReader(file1).decompressor.getData();
    }

    public static IntSequence getIntSequence(File file1) throws IOException {
        return getIntSequence(file1, 0);
    }

    public static void decompress(String route) throws IOException {
        List<Integer> data = new SaveReader(route).decompressor.getData();
        FileOutputStream output = new FileOutputStream(Paths.getRoute(unpackedName(route)));
        for (int b : data) {
            output.write(b);
        }
        output.close();
    }

    private static String unpackedName(String route) {
        return route + ".un";
    }
}
