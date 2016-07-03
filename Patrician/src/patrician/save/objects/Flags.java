package patrician.save.objects;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class Flags extends IntItem {
    public Flags() {
        super(1);
    }

    @Override
    public String toString() {
        return String.format("%h\n", getData());
    }
}
