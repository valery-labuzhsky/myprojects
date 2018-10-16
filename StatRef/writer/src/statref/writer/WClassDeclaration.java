package statref.writer;

import statref.model.SClassDeclaration;
import statref.model.SGenericDeclaration;

import java.io.IOException;
import java.util.List;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WClassDeclaration extends WBaseClassDeclaration<SClassDeclaration> {
    public WClassDeclaration() {
        super();
    }

    @Override
    public void write(CodeWriter writer, SClassDeclaration clazz) throws IOException {
        modifiersWriter().write(writer, clazz.getModifiers());
        writer.write("class ");
        writer.write(clazz.getSimpleName());
        List<SGenericDeclaration> parameters = clazz.getGenerics();
        if (!parameters.isEmpty()) {
            write(writer, parameters, "<", ", ", ">");
        }
        writeBody(writer, clazz);
    }
}
