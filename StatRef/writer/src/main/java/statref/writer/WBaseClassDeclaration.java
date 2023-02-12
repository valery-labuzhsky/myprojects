package statref.writer;

import statref.model.classes.SBaseClassDeclaration;

import java.io.IOException;

public abstract class WBaseClassDeclaration<S extends SBaseClassDeclaration> extends WBase<S> {
    public WBaseClassDeclaration() {
        super();
    }

    protected void writeBody(CodeWriter writer, S clazz) throws IOException {
        write(writer, clazz.getMembers(), "{\n", "\n\n", "\n}");
    }
}
