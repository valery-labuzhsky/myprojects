package statref.model.builder.classes;

import statref.model.expressions.SAnonClassDeclaration;
import statref.model.types.SClass;
import statref.model.expressions.SConstructor;

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
