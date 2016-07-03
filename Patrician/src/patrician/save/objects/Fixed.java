package patrician.save.objects;

import java.util.List;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class Fixed extends Sequence<Fixed> {
    public Fixed(int[] sequence) {
        super(sequence);
    }

    @Override
    public int read(int offset, List<Integer> data) {
        for (int i=0; i<sequence.length; i++) {
            if (sequence[i]!=data.get(offset+i)) {
                throw new RuntimeException("Unexpected byte at "+(offset+i));
            }
        }
        return size();
    }

    @Override
    public Fixed clone() {
        return new Fixed(sequence.clone());
    }

//    @Override
//    public String toString() {
//        return "FIXED: "+super.toString();
//    }
}
