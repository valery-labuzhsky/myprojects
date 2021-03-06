package statref.writer;

import statref.model.SElement;
import statref.model.SGenericDeclaration;
import statref.model.SLocalVariableDeclaration;
import statref.model.classes.SClassDeclaration;
import statref.model.classes.SFile;
import statref.model.expressions.*;
import statref.model.members.SFieldDeclaration;
import statref.model.members.SMethodDeclaration;
import statref.model.members.SParameterDeclaration;
import statref.model.statements.SBlock;
import statref.model.statements.SMethodStatement;
import statref.model.statements.SReturn;
import statref.model.types.SArray;
import statref.model.types.SClass;
import statref.model.types.SGeneric;
import statref.model.types.SWildcardType;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public abstract class WBase<S> {

    private static LinkedHashMap<Class<?>, WBase<?>> registry = new LinkedHashMap<>(); // TODO combine with registry from IFactory

    static {
        register(SClassDeclaration.class, new WClassDeclaration());
        register(SAnonClassDeclaration.class, new WAnonClassDeclaration());
        register(SLocalVariableDeclaration.class, new WVariableDeclaration());
        register(SFieldDeclaration.class, new WFieldDeclaration());
        register(SParameterDeclaration.class, new WParameter());
        register(SMethodDeclaration.class, new WMethodDeclaration());
        register(SField.class, new WFieldUsage());
        register(SReturn.class, new WReturn());
        register(SBlock.class, new WBlock());
        register(SClass.class, new WClassUsage());
        register(SLocalVariable.class, new WVariable());
        register(SMethod.class, new WMethodUsage());
        register(SMethodStatement.class, new WMethodInstruction());
        register(SConstructor.class, new WConstructor());
        register(SListedArrayConstructor.class, new WListedArrayConstructor());
        register(SGeneric.class, new WTypeVariable());
        register(SGenericDeclaration.class, new WTypeVariableDeclaration());
        register(SArray.class, new WArray());
        register(SWildcardType.class, new WWildcardType());
        register(SClassCast.class, new WClassCast());
        register(SArrayItem.class, new WArrayItem());
        register(SFile.class, new WFile());
        register(SElement.class, new WElement());
    }

    public WBase() {
    }

    private static <W extends WBase<S>, S> W register(Class<S> clazz, W writer) {
        registry.put(clazz, writer);
        return writer;
    }

    private static <W extends WBase<S>, S> W getWriter(Class<S> clazz) {
        for (Class<?> intf : registry.keySet()) {
            if (intf.isAssignableFrom(clazz)) {
                return (W) registry.get(intf);
            }
        }
        throw new RuntimeException("No writer is registered for " + clazz.getName());
    }

    public static WModifiers modifiersWriter() {
        return new WModifiers();
    }

    protected static <S> void write(CodeWriter writer, List<S> list, String openBracket, String separator, String closeBracket) throws IOException {
        writer.write(openBracket);
        writer.indent();
        boolean first = true;
        for (S item : list) {
            if (first) {
                first = false;
            } else {
                writer.write(separator);
            }
            writeElement(item, writer);
        }
        writer.unindent();
        writer.write(closeBracket);
    }

    public abstract void write(CodeWriter writer, S element) throws IOException;

    public static void writeElement(Object element, CodeWriter writer) throws IOException {
        getWriter(element).write(writer, element);
    }

    public static <S> WBase<S> getWriter(S element) {
        return getWriter((Class<S>) element.getClass());
    }

}
