package statref.writer;

import statref.model.types.SClass;
import statref.model.types.SType;

import java.io.IOException;
import java.util.List;

public class WClassUsage extends WBase<SClass> {
    @Override
    public void write(CodeWriter writer, SClass element) throws IOException {
        writer.write(element.getName());
        List<SType> generics = element.getGenerics();
        if (!generics.isEmpty()) {
            write(writer, generics, "<", ", ", ">");
        }
    }

}
