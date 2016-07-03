package tools.bindiff.simple;

/**
 * Created by ptasha on 14/02/15.
 */
public class DiffsStrings extends Strings<BinCompare> {
    private boolean full;

    @Override
    public boolean writeItem(Integer b1, Integer b2) {
        if (equals(b1, b2) && b1==null && getItem().isEmpty()) {
            full = true;
        }
        if (!equals(b1, b2) && item instanceof ByteString) {
            item = ((ByteString)getItem()).createDiff();
        }
        return super.writeItem(b1, b2);
    }

    @Override
    protected BinCompare createItem() {
        return new ByteString(0);
    }

    @Override
    protected BinCompare next() {
        return new ByteString(getItem().getIndex()+StringSettings.getCapacity());
    }

    @Override
    public boolean isFull() {
        return full || strings.size() > Strings.CAPACITY;
    }
}
