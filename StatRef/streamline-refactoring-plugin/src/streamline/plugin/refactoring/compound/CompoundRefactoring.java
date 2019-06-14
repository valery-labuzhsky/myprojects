package streamline.plugin.refactoring.compound;

import streamline.plugin.refactoring.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class CompoundRefactoring extends Refactoring {
    private final List<Refactoring> refactorings = new ArrayList<>();

    public List<Refactoring> getRefactorings() {
        return refactorings;
    }

    @Override
    protected void doRefactor() {
        for (Refactoring refactoring : refactorings) {
            refactoring.refactor();
        }
    }

    public void add(Refactoring refactoring) {
        refactorings.add(refactoring);
    }
}
