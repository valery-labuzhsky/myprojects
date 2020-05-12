package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleCompoundRefactoring extends CompoundRefactoring {
    private final List<Refactoring> refactorings = new ArrayList<>();

    public SimpleCompoundRefactoring(RefactoringRegistry registry, SElement element) {
        super(registry, element);
    }

    @NotNull
    @Override
    public Stream<Refactoring> getRefactorings() {
        return refactorings.stream();
    }

    public <R extends Refactoring> R add(R refactoring) {
        refactorings.add(registry.getRefactoring(refactoring));
        return refactoring;
    }

}
