package statref.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class CodeWriter {
    private final Writer writer;
    private int indent;
    private boolean newLine = true;

    public CodeWriter(Writer writer) {
        this.writer = writer;
    }

    private static String indent(int intend) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < intend; i++) {
            builder.append("    ");
        }
        return builder.toString();
    }

    public void indent() throws IOException {
        indent++;
    }

    public void unindent() throws IOException {
        indent--;
    }

    public void writeln(String string) throws IOException {
        this.write(string);
        writeln();
    }

    public void writeln() throws IOException {
        writer.write("\n");
        newLine = true;
    }

    public void write(String string) throws IOException {
        String[] split = string.split("\n", -1);
        boolean first = true;
        for (String s : split) {
            if (!first) {
                writeln();
            } else {
                first = false;
            }
            if (!s.isEmpty()) {
                if (newLine) {
                    writer.write(indent(indent));
                    newLine = false;
                }
                writer.write(s);
            }
        }
    }
}
