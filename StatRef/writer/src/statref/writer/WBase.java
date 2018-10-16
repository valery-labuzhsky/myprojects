package statref.writer;

import statref.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public abstract class WBase<S> {

    private static HashMap<Class<?>, WBase<?>> registry = new HashMap<>();

    static {
        register(SClassDeclaration.class, new WClassDeclaration());
        register(SAnonClassDeclaration.class, new WAnonClassDeclaration());
        register(SVariableDeclaration.class, new WVariableDeclaration());
        register(SFieldDeclaration.class, new WFieldDeclaration());
        register(SMethodDeclaration.class, new WMethodDeclaration());
        register(SFieldUsage.class, new WFieldUsage());
        register(SReturn.class, new WReturn());
        register(SBlock.class, new WBlock());
        register(SClass.class, new WClassUsage());
        register(SVariable.class, new WVariable());
        register(SMethod.class, new WMethodUsage());
        register(SMethodInstruction.class, new WMethodInstruction());
        register(SConstructor.class, new WConstructor());
        register(SListedArrayConstructor.class, new WListedArrayConstructor());
        register(SPrimitive.class, new WPrimitive());
        register(SGeneric.class, new WTypeVariable());
        register(SGenericDeclaration.class, new WTypeVariableDeclaration());
        register(SArray.class, new WArray());
        register(SWildcardType.class, new WWildcardType());
        register(SClassCast.class, new WClassCast());
        register(SArrayItem.class, new WArrayItem());
    }

    public WBase() {
    }

    private static <W extends WBase<S>, S> W register(Class<S> clazz, W writer) {
        registry.put(clazz, writer);
        return writer;
    }

    private static <W extends WBase<S>, S> W getWriter(Class<S> clazz) {
        Class<?> registered = null;
        for (Class<?> intf : registry.keySet()) {
            if (intf.isAssignableFrom(clazz)) {
                if (registered == null) {
                    registered = intf;
                } else {
                    boolean imr = intf.isAssignableFrom(registered);
                    boolean rmi = registered.isAssignableFrom(intf);
                    if (rmi && !imr) {
                        registered = intf;
                    } else if (!imr || rmi) {
                        throw new RuntimeException("Ambigous interfaces for " + clazz + ": " + intf + ", " + registered);
                    }
                }
            }
        }
        if (registered == null) {
            throw new RuntimeException("No writer is registered for " + clazz.getName());
        }
        return (W) registry.get(registered);
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

    private static WBase<Object> getWriter(Object element) {
        return getWriter((Class<Object>) element.getClass());
    }

}
