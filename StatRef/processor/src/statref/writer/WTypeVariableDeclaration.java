package statref.writer;

import statref.model.SGenericDeclaration;

import java.io.IOException;

public class WTypeVariableDeclaration extends WBase<SGenericDeclaration> {
    @Override
    public void write(CodeWriter writer, SGenericDeclaration element) throws IOException {
        writer.write(element.getName());
    }
}
