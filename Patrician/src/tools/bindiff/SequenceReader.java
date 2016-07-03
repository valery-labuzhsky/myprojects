package tools.bindiff;

import java.util.LinkedList;

/**
 * Created by ptasha on 10/02/15.
 */
public class SequenceReader {
    private final LinkedList<Sequence> sequences = new LinkedList<Sequence>();
    private SequenceProcessor processor;

    public SequenceReader(String name1, String name2) {
        // TODO
    }

    public void read() {
        while (processor!=null) {
            processor.read();
        }
    }

    public void addSequence(Equal equal) {
        sequences.add(equal);
    }
}