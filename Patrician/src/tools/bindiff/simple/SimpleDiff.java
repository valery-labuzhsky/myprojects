package tools.bindiff.simple;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ptasha on 12/02/15.
 */
public class SimpleDiff implements BinCompare {
    private final EqualStrings equalStrings = new EqualStrings();
    private final DiffStrings diffStrings = new DiffStrings(equalStrings);

    public boolean write(Integer b1, Integer b2) {
        if (!equalStrings.write(b1, b2)) {
            return diffStrings.write(b1, b2);
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return equalStrings.isEmpty() && diffStrings.isEmpty();
    }

    @Override
    public boolean isFull() {
        return diffStrings.isFull();
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public void print() {
        equalStrings.print();
        diffStrings.print();
    }

}
