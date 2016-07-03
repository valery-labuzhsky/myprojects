package tools.bindiff.simple;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class StringSettings {
    public static int blockSize = 4;
    public static int size = 10;
    public static int width = 2;
    public static String format = "%%%dx";

    public static int getBlockSize() {
        return blockSize;
    }

    public static int getSize() {
        return size;
    }

    public static int getCapacity() {
        return size * blockSize;
    }

    public static String format() {
        return String.format(format, width);
    }

    public static void setInt() {
        blockSize = 1;
        width = 11;
        format = "%%%dd";
    }
}
