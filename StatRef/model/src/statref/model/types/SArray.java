package statref.model.types;

public class SArray extends SType {
    private final SType type;

    public SArray(SType type) {
        this.type = type;
    }

    public SType getType() {
        return type;
    }
}
