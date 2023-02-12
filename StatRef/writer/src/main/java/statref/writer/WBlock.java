package statref.writer;

import statref.model.statements.SBlock;
import statref.model.statements.SStatement;

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
        for (SStatement SStatement : block.getInstructions()) {
            writeElement(SStatement, cw);
        }
        cw.unindent();
        cw.write("}");
    }
}
