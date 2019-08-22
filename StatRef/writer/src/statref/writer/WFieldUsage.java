package statref.writer;

import statref.model.expression.SFieldUsage;

import java.io.IOException;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class WFieldUsage extends WBase<SFieldUsage> {
    @Override
    public void write(CodeWriter cw, SFieldUsage fieldUsage) throws IOException {
        writeElement(fieldUsage.getQualifier(), cw);
        cw.write("." + fieldUsage.getName());
    }
}
