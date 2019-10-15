package streamline.plugin.refactoring.compound;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.nodes.Presenter;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.nodes.SimplePresenter;
import streamline.plugin.refactoring.Refactoring;

import java.util.ArrayList;

public class CompoundNode<R extends CompoundRefactoring> extends RefactoringNode<R> {
    public CompoundNode(R refactoring, NodesRegistry registry) {
        super(refactoring, registry);
    }

    @NotNull
    @Override
    public SimpleNode[] createChildren() {
        ArrayList<SimpleNode> nodes = new ArrayList<>();
        for (Refactoring r : refactoring.getRefactorings()) {
            nodes.add(registry.create(r));
        }
        return nodes.toArray(new SimpleNode[0]);
    }

    @Override
    protected Presenter createPresenter() {
        if (refactoring.getClass().equals(CompoundRefactoring.class)) {
            return new EmptyPresenter();
        } else {
            return new SimplePresenter().add(refactoring.toString());
        }
    }

    @Override
    public boolean showRoot() {
        return !(getPresenter() instanceof EmptyPresenter);
    }

    private static class EmptyPresenter implements Presenter {
        @Override
        public void update(PresentationData presentation) {
        }
    }
}
