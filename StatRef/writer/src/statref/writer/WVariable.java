package statref.writer;

import statref.model.expressions.SLocalVariable;

import java.io.IOException;

public class WVariable extends WBase<SLocalVariable> {
    @Override
    public void write(CodeWriter writer, SLocalVariable element) throws IOException {
        writer.write(element.getName());
    }
}
