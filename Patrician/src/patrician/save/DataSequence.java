package patrician.save;

import tools.bindiff.simple.Sequence;

import java.util.List;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class DataSequence implements Sequence {
    private int index;
    private final List<Integer> data;

    public DataSequence(List<Integer> data) {
        this.data = data;
    }

    @Override
    public void skip(int size) {
        index += size;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean hasNext() {
        return index < data.size();
    }

    @Override
    public Integer next() {
        return data.get(index++);
    }

    @Override
    public void remove() {
    }
}
