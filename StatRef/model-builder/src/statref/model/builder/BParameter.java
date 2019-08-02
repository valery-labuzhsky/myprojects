package statref.model.builder;

import statref.model.SParameter;
import statref.model.SType;

public class BParameter extends BBaseVariableDeclaration<BParameter> implements SParameter {
    public BParameter(SType type, String name) {
        super(type, name);
    }

    @Override
    public int getIndex() {
        return 0; // TODO implement
    }
}
