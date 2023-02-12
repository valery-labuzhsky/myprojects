package statref.writer;

import statref.model.statements.SMethodStatement;

import java.io.IOException;

public class WMethodInstruction extends WBase<SMethodStatement> {
    @Override
    public void write(CodeWriter writer, SMethodStatement element) throws IOException {
        writeElement(element.getMethod(), writer);
        writer.write(";");
    }
}
