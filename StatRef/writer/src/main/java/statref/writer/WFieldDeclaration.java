package statref.writer;

import statref.model.members.SFieldDeclaration;

import java.io.IOException;

public class WFieldDeclaration extends WInitializedVariableDeclaration<SFieldDeclaration> {
    @Override
    public void write(CodeWriter writer, SFieldDeclaration field) throws IOException {
        super.write(writer, field);
        writer.write(";");
    }
}
