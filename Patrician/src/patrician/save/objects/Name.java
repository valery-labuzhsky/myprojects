package patrician.save.objects;

import java.util.List;

/**
 * Created on 29/03/15.
 *
 * @author ptasha
 */
public class Name extends Sequence<Name> {
    private String name = "";

    public Name() {
        super(15);
    }

    @Override
    public int read(int offset, List<Integer> data) {
        for (int i=0; i<size(); i++) {
            int ch = data.get(offset + i);
            if (ch==0) {
                break;
            }
            name += (char)ch;
        }
        return super.read(offset, data);
    }

    @Override
    public Name clone() {
        return new Name();
    }

    @Override
    public String toString() {
        return name + "\n";
    }

    public String getName() {
        return name;
    }
}
