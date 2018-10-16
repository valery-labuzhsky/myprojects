package statref.model.builder;

import statref.model.SFieldDeclaration;
import statref.model.SType;

public class BFieldDeclaration extends BBaseVariableDeclaration<BFieldDeclaration> implements SFieldDeclaration {
    public BFieldDeclaration(SType type, String name) {
        super(type, name);
    }
}
