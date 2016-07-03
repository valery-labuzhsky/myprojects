package patrician.save.objects;

/**
 * Created on 28/03/15.
 *
 * @author ptasha
 */
public class Flag {
    private final int flag;

    public Flag(int flag) {
        this.flag = flag;
    }

    public boolean isSet(int flags) {
        return (flags & flag) !=0;
    }
}
