package statref.model.builder;

import statref.model.SType;
import statref.model.SWildcardType;

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
