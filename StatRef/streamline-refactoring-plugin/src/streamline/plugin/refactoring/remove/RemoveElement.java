package streamline.plugin.refactoring.remove;

import statref.model.idea.IInitializer;
import streamline.plugin.refactoring.Refactoring;

public class RemoveElement extends Refactoring {
    private final IInitializer initializer;

    public RemoveElement(IInitializer initializer) {
        this.initializer = initializer;
    }

    public IInitializer getInitializer() {
        return initializer;
    }

    @Override
    protected void doRefactor() {
        initializer.getElement().delete();
    }
}
