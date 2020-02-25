package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.AssignmentFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InlineUsage extends Refactoring {
    private final IVariable usage;
    private IInitializer selected;
    private final ArrayList<IInitializer> variants = new ArrayList<>();

    public InlineUsage(IVariable usage, RefactoringRegistry registry) {
        super(registry);
        this.usage = usage;
        this.variants.addAll(new AssignmentFlow(usage).getVariants(usage));
        if (variants.size() == 1) setSelected(variants.get(0));
        else if (variants.size() > 1) setEnabled(false);
    }

    @Override
    protected void doRefactor() {
        if (selected != null) {
            usage.replace(selected.getInitializer());
        }
        setEnabled(false); // TODO kludge to get rid of recursion
        for (Refactoring refactoring : whatElse()) {
            refactoring.refactor();
        }
    }

    public IVariable getUsage() {
        return usage;
    }

    public ArrayList<IInitializer> getVariants() {
        return variants;
    }

    public IInitializer getSelected() {
        return selected;
    }

    public void setSelected(IInitializer variant) {
        selected = variant;
    }

    public List<Refactoring> whatElse() {
        // TODO all this whatElse staff causing too much of recursion
        // TODO what can I do with it?
        ArrayList<Refactoring> refactorings = new ArrayList<>();
        whatElse(refactorings);
        return refactorings;
    }

    private void whatElse(ArrayList<Refactoring> refactorings) {
        if (selected != null) {
            for (IInitializer variant : variants) {
                refactorings.add(registry.getRefactoring(new InlineAssignment(registry, variant)));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InlineUsage that = (InlineUsage) o;
        return usage.equals(that.usage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usage);
    }

    @Override
    public String toString() {
        return "Replace " + usage.getText() + " with " + (selected == null ? null : selected.getText());
    }
}
