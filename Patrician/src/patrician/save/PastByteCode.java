package patrician.save;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class PastByteCode extends Code {
    private final int offset;
    private final int bytes;

    public PastByteCode(String string, int offset, int bytes) {
        super(string);
        this.offset = offset;
        this.bytes = bytes;
    }

    @Override
    public List<Integer> getData(Decompressor decompressor) {
        int past = decompressor.getLastData(offset);
        ArrayList<Integer> list = new ArrayList<>();
        past >>= 8 * (4 - bytes);
        for (int i=0; i<bytes; i++) {
            list.add(past & 0xFF);
            past >>= 8;
        }
        return list;
    }
}
