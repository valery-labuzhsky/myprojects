package statref.writer;

import statref.model.SFieldDeclaration;

import java.io.IOException;

public class WFieldDeclaration extends WBaseVariableDeclaration<SFieldDeclaration> {
    @Override
    public void write(CodeWriter writer, SFieldDeclaration field) throws IOException {
        super.write(writer, field);
        writer.write(";");
    }
}
