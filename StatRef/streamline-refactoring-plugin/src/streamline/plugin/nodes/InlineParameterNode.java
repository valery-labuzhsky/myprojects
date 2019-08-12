package streamline.plugin.nodes;

import streamline.plugin.InlineParameter;
import streamline.plugin.refactoring.compound.CompoundNode;

public class InlineParameterNode extends CompoundNode<InlineParameter> {
    public InlineParameterNode(InlineParameter refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    protected Presenter createPresenter() {
        return new SimplePresenter().
                italic().add("Inline parameter ").
                bold().add(refactoring.getParameter().getName()).
                italic().add(" of method ").
                add(refactoring.getParameter().getParent().getName());
    }
}
