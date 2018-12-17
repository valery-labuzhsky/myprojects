package statref.writer;

import statref.model.expression.SAnonClassDeclaration;

import java.io.IOException;

public class WAnonClassDeclaration extends WBaseClassDeclaration<SAnonClassDeclaration> {
    @Override
    public void write(CodeWriter writer, SAnonClassDeclaration element) throws IOException {
        writeElement(element.getConstructor(), writer);
        writeBody(writer, element);
    }
}
