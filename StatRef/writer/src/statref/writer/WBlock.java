package statref.writer;

import statref.model.SBlock;
import statref.model.SInstruction;

import java.io.IOException;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class WBlock extends WBase<SBlock> {
    @Override
    public void write(CodeWriter cw, SBlock block) throws IOException {
        cw.write("{");
        cw.writeln();
        cw.indent();
        for (SInstruction sInstruction : block.getInstructions()) {
            writeElement(sInstruction, cw);
        }
        cw.unindent();
        cw.write("}");
    }
}
