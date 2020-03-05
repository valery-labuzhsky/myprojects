package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

public class ElementBlock extends Block {
    private final IElement element;

    public ElementBlock(IElement element) {
        this.element = element;
    }

    @Override
    public boolean harvest(Visitor visitor, Cycler cycler) {
        return visitor.visit(element);
    }

}
