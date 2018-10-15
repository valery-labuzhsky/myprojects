package statref.writer;

import statref.model.SConstructor;

import java.io.IOException;

public class WConstructor extends WBase<SConstructor> {
    @Override
    public void write(CodeWriter writer, SConstructor element) throws IOException {
        writer.write("new ");
        writeElement(element.getSClass(), writer);
        write(writer, element.getParameters(), "(", ", ", ")");
    }
}
