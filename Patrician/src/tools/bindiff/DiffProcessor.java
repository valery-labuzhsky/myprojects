package tools.bindiff;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by ptasha on 10/02/15.
 */
public class DiffProcessor extends SequenceProcessor {
    private final Subprocessor sub1 = new Subprocessor();
    private final Subprocessor sub2 = new Subprocessor();

    public DiffProcessor(SequenceProcessor processor, byte b1, byte b2) {
        super(processor);
        // TODO
    }

    @Override
    public void read() {
        // TODO
        sub1.read();
        sub2.read();
        // TODO
    }

    public class Subprocessor {
        private final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        private final HashMap<Byte, Integer> map = new HashMap<Byte, Integer>();
        private byte last;

        public void read() {
            // TODO
        }
    }
}
