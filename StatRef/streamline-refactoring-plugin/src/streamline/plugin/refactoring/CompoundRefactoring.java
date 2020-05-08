package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.tree.Monkey;
import streamline.plugin.tree.NodeTreeterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompoundRefactoring extends Refactoring {
    private final List<Refactoring> refactorings = new ArrayList<>();

    public CompoundRefactoring(RefactoringRegistry registry, SElement element) {
        super(registry, element);
    }

    public List<Refactoring> getRefactorings() {
        return refactorings;
    }

    @Override
    protected void doRefactor() {
        for (Refactoring refactoring : refactorings) {
            refactoring.refactor();
        }
    }

    public <R extends Refactoring> R add(R refactoring) {
        refactorings.add(registry.getRefactoring(refactoring));
        return refactoring;
    }

    public Monkey<Refactoring> monkey() {
        return new Monkey<>(new NodeTreeterator<Refactoring>(this) {
            @Override
            protected boolean isLeaf(Refactoring node) {
                return !(node instanceof CompoundRefactoring) || ((CompoundRefactoring) node).getRefactorings().isEmpty();
            }

            @NotNull
            @Override
            protected Iterator<Refactoring> children(Refactoring node) {
                return ((CompoundRefactoring) node).getRefactorings().iterator();
            }
        });
    }
}
