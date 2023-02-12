package statref.writer;

import statref.model.expressions.SClassCast;

import java.io.IOException;

public class WClassCast extends WBase<SClassCast> {
    @Override
    public void write(CodeWriter writer, SClassCast element) throws IOException {
        writer.write("(");
        writeElement(element.getType(), writer);
        writer.write(")");
        writeElement(element.getExpression(), writer);
    }
}
