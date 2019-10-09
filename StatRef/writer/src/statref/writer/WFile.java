package statref.writer;

import statref.model.types.SClass;
import statref.model.classes.SClassDeclaration;
import statref.model.classes.SFile;

import java.io.IOException;
import java.util.List;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class WFile extends WBase<SFile> {
    public WFile() {
    }

    @Override
    public void write(CodeWriter writer, SFile file) throws IOException {
        writer.writeln("package " + file.getPackage() + ";");
        writer.writeln();

        for (SClass imp : file.getImports()) {
            writer.write("import ");
            writeElement(imp, writer);
            writer.writeln(";");
        }
        writer.writeln();

        List<SClassDeclaration> classes = file.getClasses();
        for (SClassDeclaration clazz : classes) {
            writeElement(clazz, writer);
            writer.writeln();
        }
    }
}
