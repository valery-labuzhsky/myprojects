package tools.bindiff.simple;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class IntSequence implements Sequence {
    private final Sequence sequence;

    public IntSequence(Sequence sequence, int skip) {
        this.sequence = sequence;
        this.sequence.skip(skip);
    }

    public IntSequence(Sequence sequence) {
        this(sequence, 0);
    }

    @Override
    public void skip(int size) {
        sequence.skip(size * 4);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean hasNext() {
        return sequence.hasNext();
    }

    @Override
    public Integer next() {
        int next = 0;
        int i = 0;
        while (i<4 && sequence.hasNext()) {
            next |= sequence.next() << (i * 8);
            i++;
        }
        return next;
    }

    @Override
    public void remove() {

    }
}
