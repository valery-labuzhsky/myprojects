package statref.writer;

import statref.model.expression.SListedArrayConstructor;

import java.io.IOException;

public class WListedArrayConstructor extends WBase<SListedArrayConstructor> {
    @Override
    public void write(CodeWriter writer, SListedArrayConstructor element) throws IOException {
        writer.write("new ");
        writeElement(element.getType(), writer);
        writer.write("[]");
        write(writer, element.getItems(), "{", ",\n", "}");
    }
}
