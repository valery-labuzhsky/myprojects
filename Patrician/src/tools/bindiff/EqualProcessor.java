package tools.bindiff;

import java.io.ByteArrayOutputStream;

/**
 * Created by ptasha on 10/02/15.
 */
public class EqualProcessor extends SequenceProcessor {
    private final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

    public EqualProcessor(SequenceProcessor processor) {
        super(processor);
    }

    public EqualProcessor(BinaryFile file1, BinaryFile file2, SequenceReader reader) {
        super(file1, file2, reader);
    }

    @Override
    public void read() {
        // TODO check file end
        byte b1 = file1.read();
        byte b2 = file2.read();
        if (b1==b2) {
            byteArray.write(b1);
        } else {
            if (byteArray.size()>0) {
                reader.addSequence(new Equal(byteArray));
            }
//            reader.setProcessor(new DiffProcessor(this, b1, b2));
        }
    }
}
