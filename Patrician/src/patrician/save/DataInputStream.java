package patrician.save;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 08/03/15.
 *
 * @author ptasha
 */
public class DataInputStream extends InputStream {
    private final List<Integer> data;
    private int index;

    public DataInputStream(List<Integer> data) {
        this.data = data;
    }

    @Override
    public int read() throws IOException {
        if (index==data.size()) {
            index++;
            return -1;
        } else if (index>data.size()) {
            throw new EOFException();
        }
        return data.get(index++);
    }

    @Override
    public int available() throws IOException {
        return data.size() - index;
    }
}
