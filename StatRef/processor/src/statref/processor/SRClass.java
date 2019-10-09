package statref.processor;

import statref.api.Field;
import statref.model.*;
import statref.model.builder.classes.BAnonClassDeclaration;
import statref.model.builder.classes.BClassDeclaration;
import statref.model.builder.classes.BFile;
import statref.model.builder.expressions.BExpression;
import statref.model.builder.expressions.BListedArrayConstructor;
import statref.model.builder.expressions.BVariable;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.builder.types.BClass;
import statref.model.classes.SClassDeclaration;
import statref.model.classes.SPackage;
import statref.model.members.SBaseVariableDeclaration;
import statref.model.members.SMethodDeclaration;
import statref.model.types.SClass;
import statref.model.types.SGeneric;
import statref.model.types.SType;
import statref.writer.CodeWriter;
import statref.writer.WBase;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static statref.model.builder.BBase.*;
import static statref.processor.StatRefProcessor.note;

/**
 * Created on 14/01/18.
 *
 * @author ptasha
 */
public class SRClass {
    private final SClassDeclaration sclass;

    private final HashMap<String, SMethodDeclaration> getters = new HashMap<>();
    private final HashMap<String, Map<String, SMethodDeclaration>> setters = new HashMap<>();

    public SRClass(SClassDeclaration sclass) {
        this.sclass = sclass;

        for (SMethodDeclaration method : sclass.getMethods()) {
            addMethod(method);
        }
    }

    private void addMethod(SMethodDeclaration method) {
        // TODO throws
        if (method.isPublic() && !method.isStatic()) {
            String name = method.getName();
            if (name.startsWith("get")) {
                if (method.getParameters().isEmpty()) {
                    getters.put(name.substring(3), method);
                }
            } else if (name.startsWith("set")) {
                if (method.getParameters().size() == 1) {
                    SBaseVariableDeclaration parameter = method.getParameters().get(0);
                    String property = name.substring(3);
                    String type = parameter.getType().toString();
                    Map<String, SMethodDeclaration> settersMap = setters.computeIfAbsent(property, k -> new HashMap<>());
                    settersMap.put(type, method);
                }
            }
        }
    }

    public void generate(Filer filer) throws IOException {
        JavaFileObject file = filer.createSourceFile(getFullName());
        try (Writer writer = file.openWriter()) {
            CodeWriter cw = new CodeWriter(writer);

            BFile statRefFile = new BFile(getPackage()).
                    import_(ofFullClass(Type.class)).import_(ofFullClass(Field.class));

            BClassDeclaration classDeclaration = declareClass(getSimpleName()).package_(getPackage()).public_().
                    parameters(sclass.getGenerics());
            classDeclaration.member(declareField(ofClass(getSimpleName()), "SR").public_().static_().final_().body(constructor(ofClass(getSimpleName()))));

            statRefFile.class_(classDeclaration);

            BListedArrayConstructor constructor = new BListedArrayConstructor(ofClass(Field.class));

            final SClass genericClientClass = sclass.usage(sclass.getGenerics().stream().map(SGenericDeclaration::usage).collect(Collectors.toList()));

            for (Map.Entry<String, SMethodDeclaration> entry : getters.entrySet()) {
                String name = entry.getKey();
                Map<String, SMethodDeclaration> settersMap = setters.get(name);
                note("" + name);
                if (settersMap != null) {
                    SMethodDeclaration getter = entry.getValue();
                    SType type = getter.getReturnType();
                    String typename = type.toString();
                    SMethodDeclaration setter = settersMap.get(typename);

                    BClass fieldClass = ofClass(Field.class, genericClientClass, type.getGenericType());
                    BAnonClassDeclaration anonClass = declareAnonClass(fieldClass).
                            member(new BMethodDeclaration("getObjectType") {
                                @Override
                                public void describe() {
                                    BExpression objectType = field(sclass.usage(), "class");
                                    if (!sclass.getGenerics().isEmpty()) {
                                        objectType = objectType.cast(ofClass(Class.class)).cast(ofClass(Class.class, genericClientClass));
                                    }
                                    return_(objectType);
                                }
                            }.public_().returnType(ofClass(Class.class, genericClientClass))).

                            member(new BMethodDeclaration("getValueType") {
                                @Override
                                public void describe() {
                                    if (type instanceof SGeneric) {
                                        String name = ((SGeneric) type).getName();
                                        List<SGenericDeclaration> generics = sclass.getGenerics();
                                        for (int i = 0; i < generics.size(); i++) {
                                            SGenericDeclaration generic = generics.get(i);
                                            if (generic.getName().equals(name)) {
                                                return_(field(sclass.usage(), "class").call("getTypeParameters").item(integer(i)));
                                                return;
                                            }
                                        }
                                    }
                                    return_(field(type, "class"));
                                }
                            }.public_().returnType(ofClass(Type.class))).

                            member(new BMethodDeclaration("get") {
                                       @Override
                                       public void describe() {
                                           public_();
                                           returnType(type.getGenericType());

                                           BVariable object = parameter(genericClientClass, "object");
                                           return_(object.call(getter.getName()));
                                       }
                                   }
                            ).
                            member(new BMethodDeclaration("set") {
                                @Override
                                public void describe() {
                                    public_();
                                    BVariable object = parameter(genericClientClass, "object");
                                    BVariable value = parameter(type.getGenericType(), "value");

                                    code(object.call(setter.getName(), value));
                                }
                            });

                    classDeclaration.member(declareField(fieldClass, name).public_().body(anonClass));

                    constructor.addItem(variable(name));
                }
            }

            classDeclaration.member(declareMethod("fields").public_().
                    returnType(array(ofClass(Field.class, genericClientClass, wildcard()))).
                    code(return_(constructor)));
            WBase.writeElement(statRefFile, cw);
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
