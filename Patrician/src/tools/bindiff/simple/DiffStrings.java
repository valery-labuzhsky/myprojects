package tools.bindiff.simple;

/**
 * Created by ptasha on 12/02/15.
 */
public class DiffStrings extends Strings<Diff> {
    private final EqualStrings equalStrings;

    public DiffStrings(EqualStrings equalStrings) {
        this.equalStrings = equalStrings;
    }

    @Override
    protected Diff createItem() {
        return equalStrings.getItem().createDiff();
    }

    @Override
    protected Diff next() {
        return getItem().next();
    }

    @Override
    public boolean isFull() {
        return strings.size()>CAPACITY;
    }
}
