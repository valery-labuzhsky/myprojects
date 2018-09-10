package statref.writer;

import statref.model.SExpression;
import statref.model.SVariable;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WVariable extends WBase<SVariable> {
    public WVariable() {
        super();
    }

    @Override
    public void write(CodeWriter writer, SVariable field) throws IOException {
        modifiersWriter().write(writer, field.getModifiers());
        typeUsageWriter().write(writer, field.getType());
        SExpression expression = field.getExpression();
        if (expression!=null) {
            writer.write(" = ");
            expressionWriter().write(writer, expression);
        }
        writer.write(";");
        writer.writeln();
    }

}
