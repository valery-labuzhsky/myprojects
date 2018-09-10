package statref.writer;

import statref.model.SClass;
import statref.model.SVariable;
import statref.model.SMethod;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WClass extends WBase<SClass> {
    public WClass() {
        super();
    }

    @Override
    public void write(CodeWriter writer, SClass clazz) throws IOException {
        modifiersWriter().write(writer, clazz.getModifiers());
        writer.write(" " + clazz.getSimpleName() + " {");
        writer.indent();
        writer.writeln();

        WVariable wVariable = variableWriter();
        for (SVariable field : clazz.getFields()) {
            wVariable.write(writer, field);
            writer.writeln();
        }

        WMethod wMethod = methodWriter();
        for (SMethod method : clazz.getMethods()) {
            wMethod.write(writer, method);
            writer.writeln();
        }
        writer.unindent();
        writer.writeln("}");
    }

}
