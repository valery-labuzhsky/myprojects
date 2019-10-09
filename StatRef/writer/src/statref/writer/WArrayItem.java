package statref.writer;

import statref.model.expressions.SArrayItem;

import java.io.IOException;

public class WArrayItem extends WBase<SArrayItem> {
    @Override
    public void write(CodeWriter writer, SArrayItem element) throws IOException {
        writeElement(element.getExpression(), writer);
        writer.write("[");
        writeElement(element.getIndex(), writer);
        writer.write("]");
    }
}
