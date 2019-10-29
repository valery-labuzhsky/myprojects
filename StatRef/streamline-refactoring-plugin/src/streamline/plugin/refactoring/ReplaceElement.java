package streamline.plugin.refactoring;

import statref.model.SElement;
import statref.model.idea.IElement;
import statref.model.idea.IFactory;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

public class ReplaceElement extends Refactoring {
    private final IElement target;
    private final SElement replacement;

    public ReplaceElement(RefactoringRegistry registry, IElement target, SElement replacement) {
        super(registry);
        this.target = target;
        this.replacement = replacement;
    }

    public IElement getTarget() {
        return target;
    }

    public SElement getReplacement() {
        return replacement;
    }

    @Override
    protected void doRefactor() {
        target.getElement().replace(IFactory.convert(target.getProject(), replacement).getElement());
    }
}
