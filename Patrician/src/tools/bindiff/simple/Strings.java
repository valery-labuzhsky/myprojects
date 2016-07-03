package tools.bindiff.simple;

import java.util.LinkedList;

/**
 * Created by ptasha on 12/02/15.
 */
public abstract class Strings<T extends BinCompare> implements BinCompare {
    protected static final int CAPACITY = 10;

    protected T item;

    protected final LinkedList<T> strings = new LinkedList<T>();

    protected abstract T createItem();

    protected abstract T next();

    @Override
    public boolean write(Integer b1, Integer b2) {
        if (item==null) {
            item = createItem();
        }
        if (isFull()) {
            return false;
        }
        return writeItem(b1, b2);
    }

    protected boolean writeItem(Integer b1, Integer b2) {
        if (item.write(b1, b2)) {
            if (item.isFull()) {
                nextString();
            }
            return true;
        } else {
            return false;
        }
    }

    public T getItem() {
        return item;
    }

    protected void nextString() {
        strings.add(item);
        item = next();
    }

    @Override
    public void print() {
        for (T string : strings) {
            string.print();
        }
    }

    @Override
    public boolean isEmpty() {
        return strings.isEmpty();
    }

    @Override
    public int getIndex() {
        return 0;
    }

    public static boolean equals(Integer b1, Integer b2) {
        if (b1==null) {
            return b2==null;
        } else {
            return b1.equals(b2);
        }
    }
}
