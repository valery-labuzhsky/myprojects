package statref.writer;

import statref.model.SInstruction;
import statref.model.SMethod;
import statref.model.SVariable;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WMethod extends WBase<SMethod> {
    public WMethod() {
    }

    @Override
    public void write(CodeWriter writer, SMethod method) throws IOException {
        modifiersWriter().write(writer, method.getModifiers());
        typeUsageWriter().write(writer, method.getReturnType());
        writer.write(method.getName());
        writer.write("(");
        boolean first = true;
        for (SVariable parameter : method.getParameters()) {
            if (!first) {
                writer.write(", ");
            } else {
                first = false;
            }
            variableWriter().write(writer, parameter);
        }
        writer.writeln(") {"); // TODO block writer
        writer.indent();
        WInstruction wInstruction = instructionWriter();
        for (SInstruction instruction : method.getInstructions()) {
            wInstruction.write(writer, instruction);
        }
        writer.unindent();
        writer.writeln("}");
    }
}
