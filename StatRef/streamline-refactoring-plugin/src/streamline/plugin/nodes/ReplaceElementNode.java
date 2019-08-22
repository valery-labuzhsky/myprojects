package streamline.plugin.nodes;

import streamline.plugin.ReplaceElement;

public class ReplaceElementNode extends RefactoringNode<ReplaceElement> {

    public ReplaceElementNode(ReplaceElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    protected Presenter createPresenter() {
        return new SimplePresenter().italic().add("Replace ").add(refactoring.getTarget()).add(" with ").add(refactoring.getReplacement());
    }
}
