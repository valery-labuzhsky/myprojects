package patrician.save;

import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public abstract class Code {
    private final String string;

    protected Code(String string) {
        this.string = string;
    }

    public boolean startWith(String string) {
        return this.string.startsWith(string);
    }

    public boolean is(String string) {
        return this.string.equals(string);
    }

    public abstract List<Integer> getData(Decompressor decompressor);
}
