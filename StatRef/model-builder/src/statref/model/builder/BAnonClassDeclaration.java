package statref.model.builder;

import statref.model.expression.SAnonClassDeclaration;
import statref.model.SClass;
import statref.model.expression.SConstructor;

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
