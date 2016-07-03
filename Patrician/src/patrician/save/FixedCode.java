package patrician.save;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 01/03/15.
 *
 * @author ptasha
 */
public class FixedCode extends Code {
    private final List<Integer> data;

    public FixedCode(String string, Integer... data) {
        super(string);
        this.data = Arrays.asList(data);
    }

    @Override
    public List<Integer> getData(Decompressor decompressor) {
        return data;
    }
}
