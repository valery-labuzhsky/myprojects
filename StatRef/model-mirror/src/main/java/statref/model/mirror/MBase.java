package statref.model.mirror;

import statref.model.types.SClass;
import statref.model.types.SGeneric;
import statref.model.types.SPrimitive;
import statref.model.types.SType;

import javax.lang.model.type.TypeMirror;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public abstract class MBase<TM extends TypeMirror> {
    private final TM typeMirror;

    public MBase(TM typeMirror) {
        this.typeMirror = typeMirror;
    }

    public static SType get(TypeMirror type) {
        switch (type.getKind()) {
            case DECLARED:
                return new SClass(type.toString());
            case INT:
                return new SPrimitive(int.class);
            case BYTE:
                return new SPrimitive(byte.class);
            case CHAR:
                return new SPrimitive(char.class);
            case LONG:
                return new SPrimitive(long.class);
            case FLOAT:
                return new SPrimitive(float.class);
            case SHORT:
                return new SPrimitive(short.class);
            case DOUBLE:
                return new SPrimitive(double.class);
            case BOOLEAN:
                return new SPrimitive(boolean.class);
            case TYPEVAR:
                return new SGeneric(type.toString());
        }
        throw new UnsupportedOperationException(type + " of " + type.getKind() + " is not supported yet");
    }

    protected TM getTypeMirror() {
        return typeMirror;
    }

    @Override
    public String toString() {
        return typeMirror.toString();
    }
}
