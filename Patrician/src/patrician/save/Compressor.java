package patrician.save;

import patrician.Paths;
import tools.bindiff.simple.SimpleReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created on 09/03/15.
 *
 * @author ptasha
 */
public class Compressor {
    private final List<Integer> data;

    public Compressor(List<Integer> data) {
        this.data = data;
    }

    public void write(OutputStream output) throws IOException {
        writeSize(output, data.size());
        int written = 0;
        FixedLengthBitCode code = new FixedLengthBitCode(8);
        Bits bits = new Bits();
        for (int b : data) {
            if (writeBit(output, code, 0)) {
                written++;
            }
            if (writeByte(output, code, b)) {
                written++;
            }
        }
        if (!code.isEmpty()) {
            while (!code.hasNext()) {
                code.writeBit(0);
            }
            output.write(code.next());
            written++;
        }
        written %= 4;
        if (written>0) {
            for (; written<4; written++) {
                output.write(0);
            }
        }
    }

    private boolean writeByte(OutputStream output, FixedLengthBitCode code, int b) throws IOException {
        boolean written = false;
        for (int i=0; i<8; i++) {
            code.writeBit(b & 1);
            b >>= 1;
            if (code.hasNext()) {
                output.write(code.next());
                code.clear();
                written = true;
            }
        }
        return written;
    }

    private boolean writeBit(OutputStream output, FixedLengthBitCode code, int bit) throws IOException {
        code.writeBit(bit);
        if (code.hasNext()) {
            output.write(code.next());
            code.clear();
            return true;
        }
        return true;
    }

    private void writeSize(OutputStream output, int size) throws IOException {
        for (int i=0; i<4; i++) {
            output.write(size & 0xff);
            size >>= 8;
        }
    }

    public static void main(String[] args) throws IOException {
        Compressor compressor = new Compressor(SaveReader.getData(Paths.getGame("base")));
        FileOutputStream output = new FileOutputStream(Paths.getGame("base3"));
        compressor.write(output);
        output.close();
        SimpleReader.compare(Paths.getGame("base"), Paths.getGame("base3"));
    }
}
