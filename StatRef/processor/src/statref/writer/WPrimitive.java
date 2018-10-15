package statref.writer;

import statref.model.SPrimitive;

import java.io.IOException;

class WPrimitive extends WBase<SPrimitive> {
    @Override
    public void write(CodeWriter writer, SPrimitive element) throws IOException {
        writer.write(element.getName());
    }
}
