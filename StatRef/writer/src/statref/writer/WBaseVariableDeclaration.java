package statref.writer;

import statref.model.SInitializer;
import statref.model.expression.SExpression;
import statref.model.SBaseVariableDeclaration;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public abstract class WBaseVariableDeclaration<S extends SBaseVariableDeclaration & SInitializer> extends WBase<S> {
    public WBaseVariableDeclaration() {
        super();
    }

    @Override
    public void write(CodeWriter writer, S field) throws IOException {
        modifiersWriter().write(writer, field.getModifiers());
        writeElement(field.getType(), writer);
        writer.write(" ");
        writer.write(field.getName());
        SExpression expression = field.getInitializer();
        if (expression!=null) {
            writer.write(" = ");
            writeElement(expression, writer);
        }
    }

}
