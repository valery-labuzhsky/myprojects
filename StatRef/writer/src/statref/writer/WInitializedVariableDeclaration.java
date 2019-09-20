package statref.writer;

import statref.model.SBaseVariableDeclaration;
import statref.model.SInitializer;
import statref.model.expression.SExpression;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public abstract class WInitializedVariableDeclaration<S extends SBaseVariableDeclaration & SInitializer> extends WBaseVariableDeclaration<S> {
    public WInitializedVariableDeclaration() {
        super();
    }

    @Override
    public void write(CodeWriter writer, S field) throws IOException {
        super.write(writer, field);
        SExpression expression = field.getInitializer();
        if (expression!=null) {
            writer.write(" = ");
            writeElement(expression, writer);
        }
    }
}
