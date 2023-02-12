package statref.writer;

import statref.model.members.SVariableDeclaration;

import java.io.IOException;

public abstract class WBaseVariableDeclaration<S extends SVariableDeclaration> extends WBase<S> {
    public WBaseVariableDeclaration() {
        super();
    }

    @Override
    public void write(CodeWriter writer, S field) throws IOException {
        modifiersWriter().write(writer, field.getModifiers());
        writeElement(field.getType(), writer);
        writer.write(" ");
        writer.write(field.getName());
    }


}
