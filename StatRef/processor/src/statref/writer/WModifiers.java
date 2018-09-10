package statref.writer;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collection;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WModifiers extends WBase<Collection<Modifier>> {
    public WModifiers() {
    }

    @Override
    public void write(CodeWriter writer, Collection<Modifier> element) throws IOException {
        boolean first = true;
        for (Modifier modifier : element) {
            if (!first) {
                writer.write(" ");
            } else {
                first = false;
            }
            writer.write(modifier.toString());
        }
    }
}
