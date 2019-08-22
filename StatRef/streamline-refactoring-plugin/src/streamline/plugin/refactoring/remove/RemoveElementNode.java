package streamline.plugin.refactoring.remove;

import streamline.plugin.nodes.ElementPresenter;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.nodes.RefactoringNode;

public class RemoveElementNode extends RefactoringNode<RemoveElement> {
    public RemoveElementNode(RemoveElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @Override
    protected ElementPresenter createPresenter() {
        return new RefactoringPresenter("Remove ", refactoring.getInitializer().getElement());
    }
}
