package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

import java.util.List;

public class ElementsBlock extends Block {
    private final IElement context;

    public ElementsBlock(IElement context) {
        this.context = context;
    }

    @Override
    public boolean harvest(Visitor visitor, Cycler cycler) {
        List<IElement> elements = visitor.getElements(this.context);
        boolean override = false;
        for (IElement element : elements) {
            override |= visitor.visit(element);
        }
        return override;
    }

}
