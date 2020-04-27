package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.VariableFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class InlineAssignment extends Refactoring {
    private final IInitializer initializer;
    private final ArrayList<InlineUsage> usages = new ArrayList<>();
    private final RemoveElement remove;

    public InlineAssignment(RefactoringRegistry registry, IInitializer initializer) {
        super(registry);
        this.initializer = initializer;
        remove = new RemoveElement(registry, initializer);
        VariableFlow flow = new VariableFlow(initializer.declaration());
        Collection<IVariable> usages = flow.getUsages(initializer);
        for (IVariable usage : usages) {
            // TODO here I create usage which are the same
            // TODO I must show only values which are assigned
            // TODO I should hide other values
            // TODO enabling must select a value
            // TODO selecting value must disable it
            this.usages.add(registry.getRefactoring(new InlineUsage(usage, registry)));
        }
        remove.setEnabled(!areUsagesLeft());
    }

    public InlineAssignment selectDefaultVariant() {
        for (InlineUsage usage : usages) {
            usage.setSelected(this.initializer);
        }
        return this;
    }

    public boolean areUsagesLeft() {
        boolean usagesLeft = false;
        for (InlineUsage usage : usages) {
            if (!usage.isEnabled()) {
                usagesLeft = true;
                break;
            }
        }
        return usagesLeft;
    }

    @Override
    protected void doRefactor() {
        for (InlineUsage usage : usages) {
            usage.refactor();
        }
        remove.refactor();
    }

    public IInitializer getInitializer() {
        return initializer;
    }

    public ArrayList<InlineUsage> getUsages() {
        return usages;
    }

    public RemoveElement getRemove() {
        return remove;
    }

    @Override
    public boolean enableOnly(Refactoring enabled) {
        boolean found = false;
        for (InlineUsage u : usages) {
            if (u.equals(enabled)) {
                u.setEnabled(true);
                found = true;
            }
        }
        remove.setEnabled(!areUsagesLeft());
        return found;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineAssignment that = (InlineAssignment) o;
        return initializer.equals(that.initializer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initializer);
    }

    public String toString() {
        return "" + initializer.getText();
    }
}
