package statref.model.types;

import statref.model.types.SType;

public interface SWildcardType extends SType {
    SType getBound();
    BouldType getBoundType();

    enum BouldType {
        EXTENDS, SUPER
    }
}
