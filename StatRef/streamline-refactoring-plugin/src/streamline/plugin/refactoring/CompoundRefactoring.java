package streamline.plugin.refactoring;

import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.ArrayList;
import java.util.List;

public class CompoundRefactoring extends Refactoring {
    private final List<Refactoring> refactorings = new ArrayList<>();

    public CompoundRefactoring(RefactoringRegistry registry) {
        super(registry);
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
        // TODO I can get rid of registry if I don't have this recursion madness!
        refactorings.add(registry.getRefactoring(refactoring));
        return refactoring;
    }
}
