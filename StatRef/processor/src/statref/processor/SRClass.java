package statref.processor;

import statref.model.*;
import statref.model.builder.BBase;
import statref.model.mirror.MVariable;
import statref.writer.CodeWriter;
import statref.writer.WBase;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static statref.model.builder.BBase.returnIt;
import static statref.processor.StatRefProcessor.note;

/**
 * Created on 14/01/18.
 *
 * @author ptasha
 */
public class SRClass {
    private final SClass sclass;

    private final HashMap<String, SMethod> getters = new HashMap<>();
    private final HashMap<String, Map<String, SMethod>> setters = new HashMap<>();

    public SRClass(SClass sclass) {
        this.sclass = sclass;

        for (SMethod method : sclass.getMethods()) {
            addMethod(method);
        }
    }

    private void addMethod(SMethod method) {
        // TODO throws
        if (method.isPublic() && !method.isStatic()) {
            String name = method.getName();
            if (name.startsWith("get")) {
                if (method.getParameters().isEmpty()) {
                    getters.put(name.substring(3), method);
                }
            } else if (name.startsWith("set")) {
                if (method.getParameters().size() == 1) {
                    // TODO deal with all possible types: classes, primitives, generics
                    MVariable parameter = method.getParameters().get(0);
                    String property = name.substring(3);
                    String type = parameter.getType().toString();
                    Map<String, SMethod> settersMap = setters.computeIfAbsent(property, k -> new HashMap<>());
                    settersMap.put(type, method);
                }
            }
        }
    }

    public void generate(Filer filer) throws IOException {
        JavaFileObject file = filer.createSourceFile(getFullName());
        try (Writer writer = file.openWriter()) {
            CodeWriter cw = new CodeWriter(writer);
            cw.write("package " + getPackage() + ";\n");
            cw.write("\n");
            cw.write("import java.lang.reflect.Type;\n");
            cw.write("import statref.api.Field;\n");
            cw.write("\n");
            cw.write("public class " + getSimpleName() + " {\n");
            cw.write("    public static final " + getSimpleName() + " SR = new " + getSimpleName() + "();\n");
            cw.write("\n");

            String origName = sclass.getSimpleName();
            StringBuilder names = new StringBuilder();

            for (Map.Entry<String, SMethod> entry : getters.entrySet()) {
                String name = entry.getKey();
                Map<String, SMethod> settersMap = setters.get(name);
                note("" + name);
                if (settersMap != null) {
                    SMethod getter = entry.getValue();
                    SType type = getter.getReturnType();
                    String genericTypename = type.getGenericTypename();
                    String typename = type.toString();
                    SMethod setter = settersMap.get(typename);
                    // TODO more accuracy in type checking

                    cw.write("    public Field<" + origName + ", " + genericTypename + "> " + name + " = new Field<" + origName + ", " + genericTypename + ">() {\n");

                    cw.write("        public Class<" + origName + "> getObjectType() ");

                    WBase.blockWriter().write(cw, BBase.block(returnIt().field(this.sclass, "class")));
                    cw.writeln();

                    cw.write("        public Type getValueType() { return " + typename + ".class; }\n"); // TODO it's actual type, not wrapper
                    cw.write("        public " + genericTypename + " get(" + origName + " object) { return object." + getter.getName() + "(); }\n");
                    cw.write("        public void set(" + origName + " object, " + genericTypename + " value) { object." + setter.getName() + "(value); }\n");
                    cw.write("    };\n");
                    cw.write("\n");

                    if (names.length() != 0) {
                        names.append(", ");
                    }
                    names.append(name);
                }
            }

            cw.write("    public Field<" + origName + ", ?>[] fields() {\n");
            cw.write("        return new Field[]{" + names + "};\n");
            cw.write("    }\n");

            // TODO add properties

            cw.write("}\n");
        }
    }

    public String getFullName() {
        return getPackage() + "." + getSimpleName();
    }

    private String getSimpleName() {
        return sclass.getSimpleName() + "StatRef";
    }

    private SPackage getPackage() {
        return this.sclass.getPackage();
    }

}
