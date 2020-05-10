package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.guts.Listeners;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.ArrayList;
import java.util.Objects;

public class InlineUsage extends Refactoring {
    // TODO lefties:
    //  1. Focus selected branch
    //  2. Make default variant part of inline usage node
    //  3. Select alternative branch
    //  4. Make selecting inline assignment as well as inline variable comfortable

    private final IVariable usage;
    private final IInitializer value;
    private final ArrayList<InlineUsage> variants = new ArrayList<>();

    public final Listeners onUpdate = new Listeners();

    public InlineUsage(RefactoringRegistry registry, IVariable usage, IInitializer value) {
        super(registry, usage);
        this.usage = usage;
        this.value = value;
        for (Refactoring refactoring : registry.getRefactorings(usage)) {
            if (refactoring instanceof InlineUsage) {
                InlineUsage sibling = (InlineUsage) refactoring;
                meetSibling(sibling);
                sibling.meetSibling(this);
            }
        }
    }

    @Override
    public boolean setEnabled(boolean enabled) {
        boolean changed = super.setEnabled(enabled);
        if (changed) { // TODO move it to Refactoring
            onUpdate.fire();
        }
        return changed;
    }

    private void meetSibling(InlineUsage sibling) {
        variants.add(sibling);
        sibling.onUpdate.invoke(() -> {
            if (sibling.isEnabled()) {
                setEnabled(false);
            }
        });
    }

    @Override
    protected void doRefactor() {
        usage.replace(value.getInitializer());
    }

    public IVariable getUsage() {
        return usage;
    }

    public ArrayList<InlineUsage> getVariants() {
        return variants;
    }

    public IInitializer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineUsage usage = (InlineUsage) o;
        return this.usage.equals(usage.usage) &&
                value.equals(usage.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usage, value);
    }

    @Override
    public String toString() {
        return "Replace " + usage.getText() + " with " + value.getText();
    }
}
