package statref.model.builder;

import statref.model.SClass;
import statref.model.expression.SConstructor;
import statref.model.expression.SExpression;
import statref.model.SType;

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

    public static BClass ofClass(Class<?> clazz, SType... generics) {
        return new BClass(clazz.getSimpleName(), generics);
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

    public static BWildcardType wildcard() {
        return new BWildcardType();
    }

    public static BArray array(SClass type) {
        return new BArray(type);
    }

    public static BReturn return_(SExpression constructor) {
        return new BReturn(constructor);
    }

    public static BClass ofClass(String name) {
        return new BClass(name);
    }

    public static BClass ofFullClass(Class<?> clazz) {
        return new BClass(clazz.getCanonicalName());
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
