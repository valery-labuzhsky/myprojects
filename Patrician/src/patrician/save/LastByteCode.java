package patrician.save;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class LastByteCode extends Code {
    private final int n;

    public LastByteCode(String string, int n) {
        super(string);
        this.n = n;
    }

    @Override
    public List<Integer> getData(Decompressor decompressor) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<n; i++) {
            list.add(decompressor.getLastByte());
        }
        return list;
    }
}
