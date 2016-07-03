package patrician.save.objects;

import java.util.List;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class IntItem extends Sequence<IntItem> {
    private int data;

    public IntItem(int size) {
        super(size);
    }

    public int getData() {
        return data;
    }

    @Override
    public int read(int offset, List<Integer> data) {
        for (int i=0; i<size(); i++) {
            this.data |= data.get(offset+i) << i*8;
        }
        return super.read(offset, data);
    }

    @Override
    public String toString() {
        return ""+data+"\n";
    }
}
