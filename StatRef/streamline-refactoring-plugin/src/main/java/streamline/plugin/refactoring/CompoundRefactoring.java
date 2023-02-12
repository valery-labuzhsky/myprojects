package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.tree.Monkey;
import streamline.plugin.tree.NodeTreeterator;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created on 11.05.2020.
 *
 * @author unicorn
 */
public abstract class CompoundRefactoring extends Refactoring {
    public CompoundRefactoring(RefactoringRegistry registry, SElement element) {
        super(registry, element);
    }

    @NotNull
    public abstract Stream<Refactoring> getRefactorings();

    @Override
    protected void doRefactor() {
        getRefactorings().forEach(Refactoring::refactor);
    }

    public Monkey<Refactoring> monkey() {
        return new Monkey<>(new NodeTreeterator<Refactoring>(this) {
            @Override
            protected boolean isLeaf(Refactoring node) {
                return !(node instanceof CompoundRefactoring) || ((CompoundRefactoring) node).getRefactorings().anyMatch(p -> true);
            }

            @NotNull
            @Override
            protected Iterator<Refactoring> children(Refactoring node) {
                return ((CompoundRefactoring) node).getRefactorings().iterator();
            }
        });
    }

    @Override
    public void enableAll() {
        super.enableAll();
        getRefactorings().forEachOrdered(r -> r.enableAll());
    }
}
