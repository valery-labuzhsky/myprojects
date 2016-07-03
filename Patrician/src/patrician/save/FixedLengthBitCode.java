package patrician.save;

/**
 * Created on 07/03/15.
 *
 * @author ptasha
 */
public class FixedLengthBitCode implements BitCode {
    private final int length;
    private int read;
    private int code;

    public FixedLengthBitCode(int length) {
        this.length = length;
    }

    @Override
    public void writeBit(int bit) {
        code |= bit << read;
        read++;
    }

    @Override
    public boolean hasNext() {
        return read >= length;
    }

    @Override
    public int next() {
        return code;
    }

    public void clear() {
        read = 0;
        code = 0;
    }

    @Override
    public boolean isOver() {
        return hasNext();
    }

    public int getLength() {
        return length;
    }

    public int getMaximum() {
        return (1 << length) - 1;
    }

    public boolean isMaximum() {
        return code == getMaximum();
    }

    public boolean isEmpty() {
        return read==0;
    }
}
