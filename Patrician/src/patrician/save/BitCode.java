package patrician.save;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public interface BitCode {
    void writeBit(int bit);

    boolean hasNext();

    int next();

    boolean isOver();
}
