package statref.model.mirror;

import statref.model.types.SGeneric;
import statref.model.SGenericDeclaration;

import javax.lang.model.type.TypeVariable;

public class MGenericDeclaration extends MBase<TypeVariable> implements SGenericDeclaration {
    public MGenericDeclaration(TypeVariable typeMirror) {
        super(typeMirror);
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public SGeneric usage() {
        return new SGeneric(getTypeMirror().toString());
    }
}
