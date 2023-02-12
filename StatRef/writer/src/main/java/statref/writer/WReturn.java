package statref.writer;

import statref.model.statements.SReturn;

import java.io.IOException;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class WReturn extends WBase<SReturn> {
    @Override
    public void write(CodeWriter cw, SReturn element) throws IOException {
        cw.write("return ");
        writeElement(element.getExpression(), cw);
        cw.write(";");
    }
}
