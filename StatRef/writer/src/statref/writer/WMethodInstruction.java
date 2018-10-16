package statref.writer;

import statref.model.SMethodInstruction;

import java.io.IOException;

public class WMethodInstruction extends WBase<SMethodInstruction> {
    @Override
    public void write(CodeWriter writer, SMethodInstruction element) throws IOException {
        writeElement(element.getMethod(), writer);
        writer.write(";");
    }
}
