package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

import java.util.Collections;

public class PointerCycler extends BoundaryCycler {

    public PointerCycler(AssignmentFlow flow, IElement context) {
        super(flow, new ElementsBlock(flow.getVariables().getOrDefault(context, Collections.emptyList())));
        getBlock().gotoLast();
    }

    private ElementsBlock getBlock() {
        return (ElementsBlock) this.block;
    }

    public PointerCycler startFrom(IElement element) {
        getBlock().goTo(element);
        return this;
    }

}
