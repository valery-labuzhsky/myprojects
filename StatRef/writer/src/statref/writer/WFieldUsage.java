package statref.writer;

import statref.model.expressions.SField;

import java.io.IOException;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class WFieldUsage extends WBase<SField> {
    @Override
    public void write(CodeWriter cw, SField fieldUsage) throws IOException {
        writeElement(fieldUsage.getQualifier(), cw);
        cw.write("." + fieldUsage.getName());
    }
}
