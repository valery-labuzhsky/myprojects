package tools.bindiff.simple;

/**
* Created by ptasha on 12/02/15.
*/
public class Diff implements BinCompare {
    private final ByteString string1;
    private final ByteString string2;

    public Diff(ByteString string1, ByteString string2) {
        this.string1 = string1;
        this.string2 = string2;
    }

    @Override
    public boolean write(Integer b1, Integer b2) {
        string1.write(b1, b1);
        return string2.write(b2, b2);
    }

    @Override
    public boolean isEmpty() {
        return string1.isEmpty();
    }

    @Override
    public boolean isFull() {
        return string2.isFull();
    }

    @Override
    public int getIndex() {
        return string1.getIndex();
    }

    @Override
    public void print() {
        System.out.println("1:");
        string1.print();
        System.out.println("2:");
        string2.print(string1);
    }

    public Diff next() {
        int nextIndex = string2.getIndex() + StringSettings.getCapacity();
        return new Diff(new ByteString(nextIndex), new ByteString(nextIndex));
    }
}
