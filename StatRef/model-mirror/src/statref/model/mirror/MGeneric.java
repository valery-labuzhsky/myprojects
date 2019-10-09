package statref.model.mirror;

import statref.model.types.SGeneric;

import javax.lang.model.type.TypeVariable;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public class MGeneric extends MBase<TypeVariable> implements SGeneric {
    public MGeneric(TypeVariable type) {
        super(type);
    }

    @Override
    public String getName() {
        return toString();
    }

}
