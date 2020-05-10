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
        super(registry, initializer);
        this.initializer = initializer;
        remove = new RemoveElement(registry, initializer);
        VariableFlow flow = new VariableFlow(initializer.declaration());
        Collection<IVariable> usages = flow.getUsages(initializer);
        for (IVariable usage : usages) {
            this.usages.add(registry.getRefactoring(new InlineUsage(registry, usage, initializer)));
        }
        remove.setEnabled(!areUsagesLeft());
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

    public InlineUsage enableOnly(IVariable toEnable) {
        InlineUsage enabled = null;
        for (InlineUsage usage : usages) {
            if (enabled == null && usage.getUsage().equals(toEnable)) {
                usage.setEnabled(true);
                enabled = usage;
            } else {
                usage.setEnabled(false);
            }
        }
        remove.setEnabled(!areUsagesLeft());
        return enabled;
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
