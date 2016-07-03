package tools.bindiff.simple;

/**
 * Created by ptasha on 12/02/15.
 */
public interface BinCompare {
    boolean write(Integer b1, Integer b2);
    boolean isEmpty();
    boolean isFull();
    int getIndex();
    void print();
}
