package statref.model.builder;

import statref.model.SArray;
import statref.model.SType;

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
