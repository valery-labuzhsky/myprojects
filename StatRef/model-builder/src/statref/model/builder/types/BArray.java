package statref.model.builder.types;

import statref.model.types.SArray;
import statref.model.types.SType;

public class BArray implements SArray {
    private final SType type;

    public BArray(SType type) {
        this.type = type;
    }

    @Override
    public SType getType() {
        return type;
    }
}
