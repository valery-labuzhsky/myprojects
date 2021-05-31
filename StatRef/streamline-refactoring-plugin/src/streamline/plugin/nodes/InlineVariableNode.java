package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.Presenter;
import streamline.plugin.nodes.guts.SimplePresenter;
import streamline.plugin.refactoring.InlineVariable;
import streamline.plugin.refactoring.guts.Refactoring;

public class InlineVariableNode extends CompoundNode {
    public InlineVariableNode(InlineVariable refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    public InlineVariable getRefactoring() {
        return (InlineVariable) super.getRefactoring();
    }

    @Override
    protected Presenter createPresenter() {
        return new SimplePresenter().
                italic().add("Inline variable ").
                bold().add(getRefactoring().getDeclaration().getName());
    }
}
