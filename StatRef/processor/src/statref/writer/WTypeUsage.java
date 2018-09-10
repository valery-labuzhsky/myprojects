package statref.writer;

import statref.model.SType;

import java.io.IOException;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public class WTypeUsage extends WBase<SType> {
    public WTypeUsage() {
    }

    @Override
    public void write(CodeWriter writer, SType element) throws IOException {
        writer.write(element.toString());
    }
}
