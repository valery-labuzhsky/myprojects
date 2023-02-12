package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.refactoring.CompoundRefactoring;
import streamline.plugin.refactoring.SimpleCompoundRefactoring;
import streamline.plugin.refactoring.guts.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class CompoundNode extends RefactoringNode {
    public CompoundNode(CompoundRefactoring refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(createPresenter());
    }

    @Override
    public CompoundRefactoring getRefactoring() {
        return (CompoundRefactoring) super.getRefactoring();
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> createChildren() {
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();
        getRefactoring().getRefactorings().forEach(r -> nodes.add(registry.create(r)));
        return nodes;
    }

    protected Presenter createPresenter() {
        if (isPlain()) {
            return new SimplePresenter();
        } else {
            return new SimplePresenter().add(getRefactoring().toString());
        }
    }

    private boolean isPlain() {
        return getRefactoring().getClass().equals(SimpleCompoundRefactoring.class);
    }

    @Override
    public boolean showRoot() {
        return !isPlain();
    }

}
