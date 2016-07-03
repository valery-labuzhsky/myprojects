package patrician.save;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class ChoiceCode implements BitCode {
    private static final List<Code> CODES = Arrays.asList(
            new FixedCode("110111100", 0, 0), // TODO fix me
            new LastByteCode("000000", 2),
            new LastByteCode("000010", 3),
            new PastByteCode("1001000", 0, 2),
            new PastByteCode("1001001", 0, 4)
            // TODO
    );

    private final LinkedList<Code> codes = new LinkedList<>(CODES);
    private String string = "";
    private final LinkedList<Integer> data = new LinkedList<>();

    private final Decompressor decompressor;
    private boolean over;

    public ChoiceCode(Decompressor decompressor) {
        this.decompressor = decompressor;
    }

    @Override
    public void writeBit(int bit) {
        this.string += bit;
        for (Iterator<Code> iterator = codes.iterator(); iterator.hasNext(); ) {
            Code code = iterator.next();
            if (!code.startWith(this.string)) {
                iterator.remove();
            }
        }
        if (codes.isEmpty()) {
            throw new RuntimeException("Unknown code "+ string);
        } else if (codes.size()==1) {
            Code code = codes.getFirst();
            if (code.is(string)) {
                data.addAll(code.getData(decompressor));
                over = true;
                codes.clear();
                codes.addAll(CODES);
                string = "";
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !data.isEmpty();
    }

    @Override
    public int next() {
        return data.remove();
    }

    @Override
    public boolean isOver() {
        return over;
    }
}
