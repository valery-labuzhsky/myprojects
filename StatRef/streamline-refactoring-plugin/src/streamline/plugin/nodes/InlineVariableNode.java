package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.Presenter;
import streamline.plugin.nodes.guts.SimplePresenter;
import streamline.plugin.refactoring.InlineVariable;

public class InlineVariableNode extends CompoundNode<InlineVariable> {
    public InlineVariableNode(InlineVariable refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    protected Presenter createPresenter() {
        return new SimplePresenter().
                italic().add("Inline variable ").
                bold().add(refactoring.getDeclaration().getName());
    }
}
