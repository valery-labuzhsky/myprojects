package statref.model.builder;

import statref.model.builder.classes.BAnonClassDeclaration;
import statref.model.builder.classes.BClassDeclaration;
import statref.model.builder.expressions.BFieldUsage;
import statref.model.builder.expressions.BVariable;
import statref.model.builder.members.BConstructor;
import statref.model.builder.members.BFieldDeclaration;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.builder.members.BVariableDeclaration;
import statref.model.builder.statements.BReturn;
import statref.model.expressions.SConstructor;
import statref.model.expressions.SExpression;
import statref.model.types.SArray;
import statref.model.types.SClass;
import statref.model.types.SType;
import statref.model.types.SWildcardType;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BBase {
    public static BFieldUsage field(SType type, String fieldName) {
        return new BFieldUsage(type, fieldName);
    }


    public static BMethodDeclaration declareMethod(String name) {
        return new BMethodDeclaration(name);
    }

    public static SClass ofClass(Class<?> clazz, SType... generics) {
        // TODO kind of primitive, do I need another class for primitives?
        return new SClass(clazz.getSimpleName(), generics);
    }

    public static BVariableDeclaration declareVariable(SType clazz, String name) {
        return new BVariableDeclaration(clazz, name);
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

    public static BVariable variable(String s) {
        return new BVariable(s);
    }

    public static BVariable integer(int integer) {
        return variable(""+ integer);
    }
}
