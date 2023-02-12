package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.expressions.ILocalVariable;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.ArrayList;
import java.util.Objects;

public class InlineUsage extends Refactoring {
    private final ILocalVariable usage;
    private final IInitializer value;
    private final ArrayList<InlineUsage> variants = new ArrayList<>();

    public InlineUsage(RefactoringRegistry registry, ILocalVariable usage, IInitializer value) {
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

    public ILocalVariable getUsage() {
        return usage;
    }

    public ArrayList<InlineUsage> getVariants() {
        return variants;
    }

    public IInitializer getValue() {
        return value;
    }

    @Override
    public void enableAll() {
        if (variants.isEmpty()) setEnabled(true);
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
