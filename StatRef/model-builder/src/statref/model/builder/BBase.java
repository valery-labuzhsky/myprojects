package statref.model.builder;

import statref.model.builder.classes.BAnonClassDeclaration;
import statref.model.builder.classes.BClassDeclaration;
import statref.model.builder.expressions.BField;
import statref.model.builder.expressions.BLocalVariable;
import statref.model.builder.members.BConstructor;
import statref.model.builder.members.BFieldDeclaration;
import statref.model.builder.members.BLocalVariableDeclaration;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.builder.statements.BReturn;
import statref.model.expressions.SConstructor;
import statref.model.expressions.SExpression;
import statref.model.types.*;
import statref.writer.CodeWriter;
import statref.writer.WBase;
import statref.writer.WElement;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BBase {
    public static BField field(SType type, String fieldName) {
        return new BField(type, fieldName);
    }


    public static BMethodDeclaration declareMethod(String name) {
        return new BMethodDeclaration(name);
    }

    public static SClass ofClass(Class<?> clazz, SType... generics) {
        if (clazz.isPrimitive()) {
            return new SPrimitive(clazz);
        } else {
            // TODO kind of primitive, do I need another class for primitives?
            return new SClass(clazz.getSimpleName(), generics);
        }
    }

    public static BLocalVariableDeclaration declareVariable(SType clazz, String name) {
        return new BLocalVariableDeclaration(clazz, name);
    }

    public static BAnonClassDeclaration declareAnonClass(SClass parent) {
        return new BAnonClassDeclaration(constructor(parent));
    }

    public static SConstructor constructor(SClass parent) {
        return new BConstructor(parent);
    }

    public static SWildcardType wildcard() {
        return new SWildcardType(null, null);
    }

    public static SArray array(SClass type) {
        return new SArray(type);
    }

    public static BReturn return_(SExpression constructor) {
        return new BReturn(constructor);
    }

    public static SClass ofClass(String name) {
        return new SClass(name);
    }

    public static SClass ofFullClass(Class<?> clazz) {
        return new SClass(clazz.getCanonicalName());
    }

    public static BClassDeclaration declareClass(String name) {
        return new BClassDeclaration(name);
    }

    public static BFieldDeclaration declareField(SType type, String name) {
        return new BFieldDeclaration(type, name);
    }

    public static BLocalVariable variable(String s) {
        return new BLocalVariable(s);
    }

    public static BLocalVariable integer(int integer) {
        return variable("" + integer);
    }

    public static <S> String write(S element) {
        // TODO I can use default implementations here! and move it to interfaces, writers concept is dumb
        StringWriter text = new StringWriter();
        try {
            WBase<S> writer = WBase.getWriter(element);
            if (writer instanceof WElement) {
                throw new RuntimeException("No writer is registered for " + element.getClass().getName());
            }
            writer.write(new CodeWriter(text), element);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text.toString();
    }
}
