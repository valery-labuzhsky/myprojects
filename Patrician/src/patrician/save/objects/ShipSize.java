package patrician.save.objects;

import java.util.HashSet;
import java.util.List;

/**
 * Created on 05/04/15.
 *
 * @author ptasha
 */
public class ShipSize extends IntItem {
    public ShipSize() {
        super(2);
    }

    public int getSize() {
        return ((getData() >> 3)+1)*10;
    }

    @Override
    public String toString() {
        return getSize()+"\n";
    }
}
