package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import streamline.plugin.refactoring.Refactoring;

import java.util.HashMap;

public class RefactoringRegistry {
    private final HashMap<Refactoring, Refactoring> registry = new HashMap<>();

    @NotNull
    public <R extends Refactoring> R getRefactoring(R refactoring) {
        return (R) registry.computeIfAbsent(refactoring, (k) -> refactoring);
    }

}
