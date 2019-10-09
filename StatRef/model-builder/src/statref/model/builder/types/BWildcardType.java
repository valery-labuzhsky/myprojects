package statref.model.builder.types;

import statref.model.types.SType;
import statref.model.types.SWildcardType;

public class BWildcardType implements SWildcardType {
    @Override
    public SType getBound() {
        return null;
    }

    @Override
    public BouldType getBoundType() {
        return null;
    }
}
