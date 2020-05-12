package streamline.plugin.nodes;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.refactoring.SimpleCompoundRefactoring;

import java.util.ArrayList;
import java.util.List;

public class CompoundNode<R extends SimpleCompoundRefactoring> extends RefactoringNode<R> {
    public CompoundNode(R refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(createPresenter());
    }

    @NotNull
    @Override
    public List<SelfPresentingNode> createChildren() {
        ArrayList<SelfPresentingNode> nodes = new ArrayList<>();
        refactoring.getRefactorings().forEach(r -> nodes.add(registry.create(r)));
        return nodes;
    }

    protected Presenter createPresenter() {
        if (isPlain()) {
            return new SimplePresenter();
        } else {
            return new SimplePresenter().add(refactoring.toString());
        }
    }

    private boolean isPlain() {
        return refactoring.getClass().equals(SimpleCompoundRefactoring.class);
    }

    @Override
    public boolean showRoot() {
        return !isPlain();
    }

}
