package patrician.save;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class Decompressor {
    private final Bits bits = new Bits();

    private final List<Integer> data = new ArrayList<>();
    private int size;

    private BitCode code = new FixedLengthBitCode(8*4);

    public void writeByte(int b) {
        try {
            bits.setByte(b);
            while (bits.hasNext()) {
                writeBit(bits.next());
            }
        } catch (RuntimeException e) {
            System.out.println(data.size());
            throw e;
        }
    }

    public void writeBit(int bit) {
        code.writeBit(bit);
        while (code.hasNext()) {
            if (size==0) {
                size = code.next();
                code = new CompressedCode(this);
            } else {
                int next = code.next();
                if (data.size() < size) {
                    data.add(next);
                }
            }
        }
    }

    public int getLastByte() {
        return getLastData(0);
    }

    public int getLastData(int offset) {
        return data.get(data.size() - 1 - offset);
    }

    public List<Integer> getData() {
        return data;
    }
}
