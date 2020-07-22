package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

public class ElementFlow extends ExecutionFlow {
    private final IElement element;

    public ElementFlow(IElement element) {
        this.element = element;
    }

    @Override
    public boolean harvest(Visitor visitor) {
        return visitor.visit(element);
    }

}
