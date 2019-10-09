package statref.model.builder.members;

import statref.model.members.SParameter;
import statref.model.types.SType;

public class BParameter extends BBaseVariableDeclaration<BParameter> implements SParameter {
    public BParameter(SType type, String name) {
        super(type, name);
    }

    @Override
    public int getIndex() {
        return 0; // TODO implement
    }
}
