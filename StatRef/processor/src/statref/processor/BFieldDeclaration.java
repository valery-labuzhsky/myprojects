package statref.processor;

import statref.model.SFieldDeclaration;
import statref.model.SType;
import statref.model.builder.BBaseVariableDeclaration;

public class BFieldDeclaration extends BBaseVariableDeclaration<BFieldDeclaration> implements SFieldDeclaration {
    public BFieldDeclaration(SType type, String name) {
        super(type, name);
    }
}
