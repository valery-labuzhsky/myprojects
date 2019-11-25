package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SimplePresenter;
import streamline.plugin.refactoring.ReplaceElement;

public class ReplaceElementNode extends RefactoringNode<ReplaceElement> {

    public ReplaceElementNode(ReplaceElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new SimplePresenter().italic().add("Replace ").add(this.refactoring.getTarget()).add(" with ").add(this.refactoring.getReplacement()));
    }

}
