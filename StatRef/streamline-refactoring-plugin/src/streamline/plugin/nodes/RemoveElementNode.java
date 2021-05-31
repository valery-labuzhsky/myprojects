package streamline.plugin.nodes;

import statref.model.idea.IElement;
import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.refactoring.RemoveElement;

public class RemoveElementNode extends RefactoringNode {
    public RemoveElementNode(RemoveElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new RefactoringPresenter("Remove ", ((IElement)refactoring.getElement()).getElement()));
    }

}
