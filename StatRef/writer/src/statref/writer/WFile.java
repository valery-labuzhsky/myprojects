package statref.writer;

import statref.model.SClassDeclaration;
import statref.model.SFile;

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

        List<SClassDeclaration> imports = file.getImports();
        for (SClassDeclaration imp : imports) {
            writer.writeln("import "+imp+";");
        }
        writer.writeln();

        List<SClassDeclaration> classes = file.getClasses();
        for (SClassDeclaration clazz : classes) {
            writeElement(clazz, writer);
            writer.writeln();
        }
    }
}