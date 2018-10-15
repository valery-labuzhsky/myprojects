package statref.model.builder;

import statref.model.SAnonClassDeclaration;
import statref.model.SClass;
import statref.model.SConstructor;

public class BAnonClassDeclaration extends BBaseClassDeclaration<BAnonClassDeclaration> implements SAnonClassDeclaration {

    private final SConstructor constructor;

    public BAnonClassDeclaration(SConstructor constructor) {
        super();
        this.constructor = constructor;
    }

    @Override
    public SClass usage() {
        return constructor.getSClass();
    }

    @Override
    public SConstructor getConstructor() {
        return constructor;
    }
}
