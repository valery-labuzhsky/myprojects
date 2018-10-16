package statref.writer;

import statref.model.SType;
import statref.model.SWildcardType;

import java.io.IOException;

public class WWildcardType extends WBase<SWildcardType> {
    @Override
    public void write(CodeWriter writer, SWildcardType element) throws IOException {
        writer.write("?");
        SType bound = element.getBound();
        if (bound != null) {
            writer.write(" ");
            writer.write(element.getBoundType().name().toLowerCase());
            writer.write(" ");
            writeElement(element.getBound(), writer);
        }
    }
}
