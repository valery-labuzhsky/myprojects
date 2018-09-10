package statref.writer;

import statref.model.SInstruction;
import statref.model.SReturn;

import java.io.IOException;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public class WInstruction extends WBase<SInstruction> {
    public WInstruction() {
    }

    @Override
    public void write(CodeWriter writer, SInstruction element) throws IOException {
        if (element instanceof SReturn) {
            returnWriter().write(writer, (SReturn) element);
        }
        // TODO
    }
}
