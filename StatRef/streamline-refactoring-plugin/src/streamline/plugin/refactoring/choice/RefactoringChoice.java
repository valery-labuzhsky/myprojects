package streamline.plugin.refactoring.choice;

import streamline.plugin.refactoring.Refactoring;

import java.util.ArrayList;

public class RefactoringChoice extends Refactoring {
    private Refactoring chosen;
    private final ArrayList<Refactoring> variants = new ArrayList<>();

    public RefactoringChoice() {
    }

    public Refactoring getChosen() {
        return chosen;
    }

    public boolean setChosen(Refactoring chosen) {
        if (chosen != this.chosen) {
            this.chosen = chosen;
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void doRefactor() {
        chosen.refactor();
    }

    public void add(Refactoring variant) {
        variants.add(variant);
    }

    public ArrayList<Refactoring> getVariants() {
        return variants;
    }
}
