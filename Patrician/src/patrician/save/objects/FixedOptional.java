package patrician.save.objects;

import java.util.List;

/**
 * Created on 04/04/15.
 *
 * @author ptasha
 */
public class FixedOptional extends Fixed {
    private boolean met;

    public FixedOptional(int[] sequence) {
        super(sequence);
    }

    @Override
    public int read(int offset, List<Integer> data) {
        try {
            int read = super.read(offset, data);
            met = true;
//            System.out.println("FIT: "+read);
            return super.size();
        } catch (Exception e) {
//            e.printStackTrace(System.out);
            return 0;
        }
    }

    @Override
    public FixedOptional clone() {
        return new FixedOptional(getSequence());
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String toString() {
        if (met) {
            return super.toString();
        } else {
            return "\n";
        }
    }
}
