package statref.model.builder;

import statref.model.SType;
import statref.model.SVariableDeclaration;

public class BVariableDeclaration extends BBaseVariableDeclaration<BVariableDeclaration> implements SVariableDeclaration {
    public BVariableDeclaration(SType type, String name) {
        super(type, name);
    }
}
