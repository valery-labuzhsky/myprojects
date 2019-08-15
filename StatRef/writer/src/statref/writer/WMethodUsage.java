package statref.writer;

import statref.model.expression.SExpression;
import statref.model.expression.SMethod;

import java.io.IOException;

public class WMethodUsage extends WBase<SMethod> {
    @Override

    public void write(CodeWriter writer, SMethod element) throws IOException {
        SExpression callee = element.getQualifier();
        if (callee != null) {
            writeElement(callee, writer);
            writer.write(".");
        }
        writer.write(element.getName());
        write(writer, element.getParams(), "(", ", ", ")");
    }

}
