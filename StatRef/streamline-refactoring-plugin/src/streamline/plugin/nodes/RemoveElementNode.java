package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.refactoring.RemoveElement;

public class RemoveElementNode extends RefactoringNode<RemoveElement> {
    public RemoveElementNode(RemoveElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new RefactoringPresenter("Remove ", this.refactoring.getInitializer().getElement()));
    }

}
