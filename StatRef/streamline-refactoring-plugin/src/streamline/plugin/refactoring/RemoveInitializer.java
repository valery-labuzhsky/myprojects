package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

public class RemoveInitializer extends Refactoring {
    private final IInitializer initializer;

    public RemoveInitializer(RefactoringRegistry registry, IInitializer initializer) {
        super(registry, initializer);
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