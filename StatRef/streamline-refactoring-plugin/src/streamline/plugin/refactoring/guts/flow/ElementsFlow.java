package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

import java.util.List;

public class ElementsFlow extends ExecutionFlow {
    private final IElement context;

    public ElementsFlow(IElement context) {
        this.context = context;
    }

    @Override
    public boolean harvest(Visitor visitor) {
        List<IElement> elements = visitor.getElements(this.context);
        boolean override = false;
        for (IElement element : elements) {
            override |= visitor.visit(element);
        }
        return override;
    }

}
