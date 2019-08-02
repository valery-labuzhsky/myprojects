package statref.model.builder;

import statref.model.SElement;
import statref.writer.CodeWriter;
import statref.writer.WBase;
import statref.writer.WElement;

import java.io.IOException;
import java.io.StringWriter;

public abstract class BElement implements SElement {
    @Override
    public SElement getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText() {
        // TODO I can use default implementations here! and move it to interfaces, writers concept is dumb
        StringWriter text = new StringWriter();
        try {
            WBase<SElement> writer = WBase.getWriter(this);
            if (writer instanceof WElement) {
                throw new RuntimeException("No writer is registered for "+getClass().getName());
            }
            writer.write(new CodeWriter(text), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text.toString();
    }
}
