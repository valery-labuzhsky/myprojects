package tools.bindiff.simple;

/**
 * Created by ptasha on 12/02/15.
 */
public class EqualStrings extends Strings<ByteString> {
    private boolean full = false;

    @Override
    public boolean write(Integer b1, Integer b2) {
        if (!equals(b1, b2)) {
            full = true;
        }
        if (equals(b1, b2) && b1==null && getItem().isEmpty()) {
            full = true;
        }
        return super.write(b1, b2);
    }

    @Override
    protected ByteString createItem() {
        return new ByteString(0);
    }

    @Override
    protected ByteString next() {
        return getItem().next();
    }

    @Override
    public boolean isFull() {
        return full;
    }

    @Override
    protected void nextString() {
        super.nextString();
        if (strings.size()> CAPACITY) {
            strings.removeFirst();
        }
    }
}
