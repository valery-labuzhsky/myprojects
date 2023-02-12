package statref.model.types;

public class SWildcardType extends SType {
    private final SType bound;
    private final BoundType boundType;

    public SWildcardType(SType bound, BoundType boundType) {
        this.bound = bound;
        this.boundType = boundType;
    }

    public SType getBound() {
        return bound;
    }

    public BoundType getBoundType() {
        return boundType;
    }

    public enum BoundType {
        EXTENDS, SUPER
    }
}
