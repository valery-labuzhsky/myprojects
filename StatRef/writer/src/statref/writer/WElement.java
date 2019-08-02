package statref.writer;

import statref.model.SElement;

import java.io.IOException;

public class WElement extends WBase<SElement> {
    @Override
    public void write(CodeWriter writer, SElement element) throws IOException {
        writer.write(element.getText());
    }
}
