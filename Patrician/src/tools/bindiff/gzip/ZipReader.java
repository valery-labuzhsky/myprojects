package tools.bindiff.gzip;

import tools.bindiff.simple.SimpleReader;

import java.io.*;
import java.util.zip.*;

/**
 * Created on 23/02/15.
 *
 * @author ptasha
 */
public class ZipReader {
    public static void main(String[] args) throws IOException {
        unpack("test.rou");
        unpack("test2.rou");
//        SimpleReader.compare("test.rou.un", "test2.rou.un");
//        Writer.print(output.toByteArray());
    }

    public static void unpack(String route) throws IOException {
        String folder = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/AutoRoute/";
//        Deflater deflater = new Deflater(Deflater.NO_COMPRESSION, false);
//        deflater.setStrategy(Deflater.HUFFMAN_ONLY);
        Inflater inflater = new Inflater(true);
        FileInputStream file = new FileInputStream(folder + route);
        InputStream gzip = new InflaterInputStream(file, inflater);
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileOutputStream output = new FileOutputStream(folder+route+".un");
        int b;
        int count = 0;
        try {
            while ((b=gzip.read())!=-1) {
                output.write(b);
                count++;
            }
        } catch (IOException e) {
            System.out.println(count);
            throw e;
        }
        output.close();
    }
}
