package streamline.plugin.refactoring.guts;

import org.jetbrains.annotations.NotNull;
import statref.model.SElement;

import java.util.ArrayList;
import java.util.HashMap;

public class RefactoringRegistry {
    private final HashMap<Refactoring, Refactoring> registry = new HashMap<>();
    private final HashMap<SElement, ArrayList<Refactoring>> perElement = new HashMap<>();

    @SuppressWarnings("unchecked")
    @NotNull
    public <R extends Refactoring> R getRefactoring(R refactoring) {
        return (R) registry.computeIfAbsent(refactoring, (k) -> {
            perElement.computeIfAbsent(refactoring.getElement(), e -> new ArrayList<>()).add(refactoring);
            return refactoring;
        });
    }

}
