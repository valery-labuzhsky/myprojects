package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.nodes.guts.RefactoringNode;
import streamline.plugin.nodes.guts.SimplePresenter;
import streamline.plugin.refactoring.ReplaceElement;
import streamline.plugin.refactoring.guts.Refactoring;

public class ReplaceElementNode extends RefactoringNode {

    public ReplaceElementNode(ReplaceElement refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(new SimplePresenter().italic().add("Replace ").add(this.getRefactoring().getTarget()).add(" with ").add(this.getRefactoring().getReplacement()));
    }

    @Override
    public ReplaceElement getRefactoring() {
        return (ReplaceElement) super.getRefactoring();
    }
}
