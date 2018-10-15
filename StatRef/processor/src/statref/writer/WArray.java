package statref.writer;

import statref.model.SArray;

import java.io.IOException;

public class WArray extends WBase<SArray> {
    @Override
    public void write(CodeWriter writer, SArray element) throws IOException {
        writeElement(element.getType(), writer);
        writer.write("[]");
    }
}
