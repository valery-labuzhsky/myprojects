package statref.model;

public interface SWildcardType extends SType {
    SType getBound();
    BouldType getBoundType();

    enum BouldType {
        EXTENDS, SUPER
    }
}
