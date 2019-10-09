package statref.writer;

import statref.model.expressions.SVariable;

import java.io.IOException;

public class WVariable extends WBase<SVariable> {
    @Override
    public void write(CodeWriter writer, SVariable element) throws IOException {
        writer.write(element.getName());
    }
}
