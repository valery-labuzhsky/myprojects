package patrician.save;

/**
 * Created on 07/03/15.
 *
 * @author ptasha
 */
public class SpecialCode implements BitCode {
    private final Decompressor decompressor;

    private FixedLengthBitCode code = new FixedLengthBitCode(3);
    private int offsetLength;
    private int offset;
    private int number = 2;

    public SpecialCode(Decompressor decompressor) {
        this.decompressor = decompressor;
    }

    @Override
    public void writeBit(int bit) {
        code.writeBit(bit);
        if (code.hasNext()) {
            if (offsetLength == 0) {
                offsetLength = code.next() + 1;
                code = new FixedLengthBitCode(offsetLength);
            } else if (offset == 0) {
                int next = code.next();
                int before = (1 << (offsetLength)) - 2;
                offset = decompressor.getData().size() - next - before;
                code = new FixedLengthBitCode(2);
            } else {
                number += code.next();
                if (code.isMaximum()) {
                    code = new FixedLengthBitCode(code.getLength()+1);
                } else {
                    offset += number - 1;
                    code = null;
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return code==null && number > 0;
    }

    @Override
    public int next() {
        return decompressor.getData().get(offset-number--);
    }

    @Override
    public boolean isOver() {
        return code == null && number==0;
    }
}
