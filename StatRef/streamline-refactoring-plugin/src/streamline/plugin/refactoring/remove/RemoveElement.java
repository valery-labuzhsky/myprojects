package streamline.plugin.refactoring.remove;

import statref.model.idea.IInitializer;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;

public class RemoveElement extends Refactoring {
    private final IInitializer initializer;

    public RemoveElement(RefactoringRegistry registry, IInitializer initializer) {
        super(registry);
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
