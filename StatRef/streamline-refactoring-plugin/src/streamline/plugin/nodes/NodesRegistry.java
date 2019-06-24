package streamline.plugin.nodes;

import streamline.plugin.refactoring.Listeners;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;

import java.util.HashMap;

public class NodesRegistry {
    private final RefactoringRegistry refactorings = new RefactoringRegistry();
    private final HashMap<Refactoring, Listeners> listeners = new HashMap<>();

    public RefactoringRegistry getRefactorings() {
        return refactorings;
    }

    public Listeners getListeners(Refactoring refactoring) {
        return listeners.computeIfAbsent(refactoring, (r) -> new Listeners());
    }
}
