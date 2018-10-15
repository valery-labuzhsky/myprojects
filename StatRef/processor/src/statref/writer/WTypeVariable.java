package statref.writer;

import statref.model.SGeneric;

import java.io.IOException;

public class WTypeVariable extends WBase<SGeneric> {
    @Override
    public void write(CodeWriter writer, SGeneric element) throws IOException {
        writer.write(element.getName());
    }
}
