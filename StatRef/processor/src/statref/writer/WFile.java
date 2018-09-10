package statref.writer;

import statref.model.SClass;
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

        List<SClass> imports = file.getImports();
        for (SClass imp : imports) {
            writer.writeln("import "+imp+";");
        }
        writer.writeln();

        List<SClass> classes = file.getClasses();
        WClass wClass = classWriter();
        for (SClass clazz : classes) {
            wClass.write(writer, clazz);
            writer.writeln();
        }
    }
}
