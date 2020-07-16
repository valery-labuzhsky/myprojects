package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.refactoring.RemoveInitializer;

public class RemoveElementNode extends RefactoringNode<RemoveInitializer> {
    public RemoveElementNode(RemoveInitializer refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new RefactoringPresenter("Remove ", this.refactoring.getInitializer().getElement()));
    }

}
