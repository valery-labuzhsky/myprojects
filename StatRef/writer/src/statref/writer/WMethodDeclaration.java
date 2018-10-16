package statref.writer;

import statref.model.SInstruction;
import statref.model.SMethodDeclaration;

import java.io.IOException;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WMethodDeclaration extends WBase<SMethodDeclaration> {
    public WMethodDeclaration() {
    }

    @Override
    public void write(CodeWriter writer, SMethodDeclaration method) throws IOException {
        modifiersWriter().write(writer, method.getModifiers());
        writeElement(method.getReturnType(), writer);
        writer.write(" ");
        writer.write(method.getName());
        write(writer, method.getParameters(), "(", ", ", ")");
        writer.writeln(" {"); // TODO block writer
        writer.indent();
        for (SInstruction instruction : method.getInstructions()) {
            writeElement(instruction, writer);
            writer.writeln();
        }
        writer.unindent();
        writer.write("}");
    }
}
