package streamline.plugin.refactoring.remove;

import statref.model.idea.IElement;
import streamline.plugin.refactoring.Refactoring;

public class RemoveElement extends Refactoring {
    private final IElement element;

    public RemoveElement(IElement element) {
        this.element = element;
    }

    public IElement getElement() {
        return element;
    }

    @Override
    protected void doRefactor() {
        element.getElement().delete();
    }
}
