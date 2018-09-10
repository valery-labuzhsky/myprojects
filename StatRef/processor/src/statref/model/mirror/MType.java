package statref.model.mirror;

import statref.model.SType;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.Arrays;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public abstract class MType<TM extends TypeMirror> implements SType {
    private final TM typeMirror;

    public MType(TM typeMirror) {
        this.typeMirror = typeMirror;
    }

    public static SType get(TypeMirror type) {
        switch (type.getKind()) {
            case DECLARED:
                return new MClass((DeclaredType) type);
            case INT:
            case BYTE:
            case CHAR:
            case LONG:
            case FLOAT:
            case SHORT:
            case DOUBLE:
            case BOOLEAN:
                return new MPrimitive((PrimitiveType)type);
            case TYPEVAR:
                return new MTypeVariable((TypeVariable)type);
        }
        throw new UnsupportedOperationException(type+" of "+type.getKind()+" is not supported yet");
    }

    protected TM getTypeMirror() {
        return typeMirror;
    }

    @Override
    public SType getGenericType() {
        return this;
    }

    @Override
    public String toString() {
         return typeMirror.toString();
    }
}
