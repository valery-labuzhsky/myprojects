package streamline.plugin.refactoring.usage;

import com.intellij.openapi.project.Project;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;
import streamline.plugin.refactoring.assignment.InlineAssignment;

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

    public Project getProject() {
        return usage.getProject();
    }

    public List<Refactoring> whatElse() {
        ArrayList<Refactoring> refactorings = new ArrayList<>();
        whatElse(refactorings);
        return refactorings;
    }

    private void whatElse(ArrayList<Refactoring> refactorings) {
        if (selected != null) {
            for (IInitializer variant : variants) {
                refactorings.add(new InlineAssignment(registry, variant));
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
