package streamline.plugin.refactoring;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

public class RemoveElement extends Refactoring {
    private final IElement element;

    public RemoveElement(RefactoringRegistry registry, IElement element) {
        super(registry, element);
        this.element = element;
    }

    @Override
    protected void doRefactor() {
        element.getElement().delete();
    }

    @Override
    public String toString() {
        return "Remove " + element.getText();
    }
}
