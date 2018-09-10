package statref.model.mirror;

import statref.model.SType;
import statref.model.reflect.RClass;

import javax.lang.model.type.PrimitiveType;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class MPrimitive extends MType<PrimitiveType> {
    public MPrimitive(PrimitiveType type) {
        super(type);
    }

    @Override
    public SType getGenericType() {
        return new RClass(getWrapperClass());
    }

    private Class getWrapperClass() {
        switch (getTypeMirror().getKind()) {
            case INT:
                return Integer.class;
            case BOOLEAN:
                return Boolean.class;
            case DOUBLE:
                return Double.class;
            case SHORT:
                return Short.class;
            case FLOAT:
                return Float.class;
            case LONG:
                return Long.class;
            case CHAR:
                return Character.class;
            case BYTE:
                return Byte.class;
            case VOID:
                return Void.class;
        }
        throw new UnsupportedOperationException("Not yet supported for " + getTypeMirror().getKind());
    }
}
