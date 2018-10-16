package statref.writer;

import statref.model.SClass;
import statref.model.SType;

import java.io.IOException;
import java.util.List;

public class WClassUsage extends WBase<SClass> {
    @Override
    public void write(CodeWriter writer, SClass element) throws IOException {
        writer.write(element.getSimpleName());
        List<SType> generics = element.getGenerics();
        if (!generics.isEmpty()) {
            write(writer, generics, "<", ", ", ">");
        }
    }

}