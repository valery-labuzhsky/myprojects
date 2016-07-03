package tools.bindiff.gzip;

import tools.bindiff.simple.BinCompare;
import tools.bindiff.simple.DiffsStrings;

import java.io.*;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created on 23/02/15.
 *
 * @author ptasha
 */
public class Writer {
    public static void main(String[] args) throws IOException {
        String folder = "/home/ptasha/PlayOnLinux's virtual drives/Patrician_3/drive_c/Program Files/Patrician III/Save/AutoRoute/";
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileOutputStream out = new FileOutputStream(folder+"def");
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
//        deflater.setStrategy(Deflater.FILTERED);
//        OutputStream gzip = new GZIPOutputStream(out);
        OutputStream gzip = new DeflaterOutputStream(out, deflater);
        for (int i=0; i<100; i++) {
//            gzip.write(0xff);
            gzip.write(i);
        }
        gzip.close();
//        byte[] bytes = out.toByteArray();
//        print(bytes);
        // 78  1 63 64  | a4 3d  0  0  | 14 1e  0 65
        // 78  1  5 c1  |  1  9  0  0  |  0  0 90 fc

        // b8  1  0  0  |  0  0 68 50  | 60 c0  4  4  |  2  0 44 28  | c0 a0 41  0  |  8  f 24 10  | 48 60 60 81  | 82  3  e 2a  | 5c b0 40  1
    }

    public static void print(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        BinCompare diff = new DiffsStrings();
        int b;
        while ((b=input.read())!=-1) {
            diff.write(b, b);
        }
        while (!diff.isFull()) {
            diff.write(-1, -1);
        }
        diff.print();
    }
}
