package tools.bindiff;

/**
 * Created by ptasha on 10/02/15.
 */
public abstract class SequenceProcessor {
    protected final BinaryFile file1;
    protected final BinaryFile file2;

    protected final SequenceReader reader;

    protected SequenceProcessor(SequenceProcessor processor) {
        this.file1 = processor.file1;
        this.file2 = processor.file2;
        this.reader = processor.reader;
    }

    protected SequenceProcessor(BinaryFile file1, BinaryFile file2, SequenceReader reader) {
        this.file1 = file1;
        this.file2 = file2;
        this.reader = reader;
    }

    public abstract void read();
}
