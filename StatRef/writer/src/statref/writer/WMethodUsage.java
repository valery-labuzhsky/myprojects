package statref.writer;

import statref.model.SMethod;

import java.io.IOException;

public class WMethodUsage extends WBase<SMethod> {
    @Override
    public void write(CodeWriter writer, SMethod element) throws IOException {
        writeElement(element.getExpression(), writer);
        writer.write(".");
        writer.write(element.getMethodName());
        write(writer, element.getParams(), "(", ", ", ")");
    }

}
