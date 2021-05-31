package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.Presenter;
import streamline.plugin.nodes.guts.SimplePresenter;
import streamline.plugin.refactoring.InlineParameter;
import streamline.plugin.refactoring.SimpleCompoundRefactoring;

public class InlineParameterNode extends CompoundNode {
    public InlineParameterNode(InlineParameter refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    public InlineParameter getRefactoring() {
        return (InlineParameter) super.getRefactoring();
    }

    @Override
    protected Presenter createPresenter() {
        return new SimplePresenter().
                italic().add("Inline parameter ").
                bold().add(getRefactoring().getParameter().getName()).
                italic().add(" of method ").
                add(getRefactoring().getParameter().getParent().getName());
    }
}
