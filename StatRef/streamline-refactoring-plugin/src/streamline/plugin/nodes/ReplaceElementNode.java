package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.Presenter;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SimplePresenter;
import streamline.plugin.refactoring.ReplaceElement;

public class ReplaceElementNode extends RefactoringNode<ReplaceElement> {

    public ReplaceElementNode(ReplaceElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    protected Presenter createPresenter() {
        return new SimplePresenter().italic().add("Replace ").add(refactoring.getTarget()).add(" with ").add(refactoring.getReplacement());
    }
}
