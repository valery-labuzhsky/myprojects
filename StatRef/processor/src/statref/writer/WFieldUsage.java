package statref.writer;

import statref.model.builder.BFieldUsage;

import java.io.IOException;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class WFieldUsage extends WBase<BFieldUsage> {
    @Override
    public void write(CodeWriter cw, BFieldUsage fieldUsage) throws IOException {
        typeUsageWriter().write(cw, fieldUsage.getType());
        cw.write("." + fieldUsage.getName());
    }
}
