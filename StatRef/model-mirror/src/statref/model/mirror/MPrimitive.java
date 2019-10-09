package statref.model.mirror;

import statref.model.types.SClass;
import statref.model.types.SType;
import statref.model.reflect.RClass;

import javax.lang.model.type.PrimitiveType;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class MPrimitive extends MBase<PrimitiveType> implements SClass {
    public MPrimitive(PrimitiveType type) {
        super(type);
    }

    @Override
    public String getName() {
        return getTypeMirror().toString();
    }

    @Override
    public Class<?> getJavaClass() {
        switch (getTypeMirror().getKind()) {
            case INT:
                return int.class;
            case BOOLEAN:
                return boolean.class;
            case DOUBLE:
                return double.class;
            case SHORT:
                return short.class;
            case FLOAT:
                return float.class;
            case LONG:
                return long.class;
            case CHAR:
                return char.class;
            case BYTE:
                return byte.class;
            case VOID:
                return void.class;
        }
        throw new UnsupportedOperationException("Not yet supported for " + getTypeMirror().getKind());
    }

    @Override
    public SType getGenericType() {
        return new RClass(getWrapperClass());
    }

    private Class getWrapperClass() {
        // TODO it "duplicates" getJavaClass
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
