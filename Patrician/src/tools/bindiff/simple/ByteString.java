package tools.bindiff.simple;

import java.util.ArrayList;

/**
* Created by ptasha on 12/02/15.
*/
public class ByteString implements BinCompare {
    private final int index;
    private final ArrayList<Integer> bytes;

    public ByteString(int index) {
        this(index, new ArrayList<Integer>(StringSettings.getCapacity()));
    }

    private ByteString(int index, ArrayList<Integer> bytes) {
        this.index = index;
        this.bytes = bytes;
    }

    @Override
    public boolean write(Integer b1, Integer b2) {
        if (isFull()) {
            return false;
        }
        bytes.add(b1);
        return true;
    }

    @Override
    public boolean isFull() {
        return bytes.size() == StringSettings.getCapacity();
    }

    public Integer get(int index) {
        return bytes.get(index);
    }

    public int getIndex() {
        return index;
    }

    public Diff createDiff() {
        return new Diff(this, copy());
    }

    private ByteString copy() {
        return new ByteString(index, new ArrayList<>(bytes));
    }

    public ByteString next() {
        return new ByteString(getIndex() + StringSettings.getCapacity());
    }

    public boolean isEmpty() {
        return bytes.isEmpty();
    }

    public void print(ByteString string2) {
        diffPrinter.string2 = string2;
        print(diffPrinter);
    }

    public void print() {
        print(bytePrinter);
    }

    private void print(BytePrinter printer) {
        System.out.print(String.format("%8d", this.getIndex()));
        for (int i=0; i<StringSettings.getSize(); i++) {
            System.out.print(" ");
            if (i!=0) {
                System.out.print("| ");
            }
            for (int j=0; j<StringSettings.getBlockSize(); j++) {
                int idx = i * StringSettings.getBlockSize() + j;
                printer.print(idx);
            }
        }
        System.out.print("  ");
        System.out.println();
    }

    private final BytePrinter bytePrinter = new BytePrinter();
    private final DiffBytePrinter diffPrinter = new DiffBytePrinter();

    private static String repeat(String str, int n) {
        String ret = "";
        for (int i=0; i<n; i++) {
            ret += str;
        }
        return ret;
    }

    private class BytePrinter {
        public void print(int idx) {
            Integer b = ByteString.this.get(idx);
            if (b==null) {
                System.out.print(repeat("-", StringSettings.width)+" ");
            } else {
                System.out.print(String.format(StringSettings.format()+" ", b));
            }
        }
    }

    private class DiffBytePrinter extends BytePrinter {
        private ByteString string2;

        public void print(int idx) {
            Integer b = ByteString.this.get(idx);
            Integer c = string2.get(idx);
            if (Strings.equals(c, b)) {
                System.out.print(repeat("=", StringSettings.width)+" ");
            } else {
                super.print(idx);
            }
        }
    }
}
