package statref.model.builder.members;

import statref.model.members.SParameterDeclaration;
import statref.model.types.SType;

public class BParameterDeclaration extends BVariableDeclaration<BParameterDeclaration> implements SParameterDeclaration {
    public BParameterDeclaration(SType type, String name) {
        super(type, name);
    }

    @Override
    public int getIndex() {
        return 0; // TODO implement
    }
}
