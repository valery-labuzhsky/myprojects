package patrician.save;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class CompressedCode implements BitCode {
    private final Decompressor decompressor;
    private BitCode code;

    public CompressedCode(Decompressor decompressor) {
        this.decompressor = decompressor;
    }

    @Override
    public void writeBit(int bit) {
        if (code==null) {
            code = bit==0? new RawCode():new SpecialCode(decompressor);
        } else {
            code.writeBit(bit);
        }
    }

    @Override
    public boolean hasNext() {
        return code!=null && code.hasNext();
    }

    @Override
    public int next() {
        int next = code.next();
        if (code.isOver()) {
            code = null;
        }
        return next;
    }

    @Override
    public boolean isOver() {
        return false;
    }
}
