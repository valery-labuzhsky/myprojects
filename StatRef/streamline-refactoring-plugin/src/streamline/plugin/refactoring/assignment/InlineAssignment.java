package streamline.plugin.refactoring.assignment;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.remove.RemoveElement;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.RefactoringRegistry;

import java.util.ArrayList;

public class InlineAssignment extends Refactoring {
    private final IInitializer initializer;
    private final ArrayList<InlineUsage> usages = new ArrayList<>();
    private final RemoveElement remove;

    public InlineAssignment(RefactoringRegistry registry, IInitializer initializer) {
        super(registry);
        this.initializer = initializer;
        remove = new RemoveElement(registry, initializer);
        for (IVariable usage : initializer.valueUsages()) {
            usages.add(registry.getRefactoring(new InlineUsage(usage, registry)));
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

    public void setOnlyEnabled(InlineUsage usage) {
        for (InlineUsage u : usages) {
            u.setEnabled(u.equals(usage));
        }
        remove.setEnabled(!areUsagesLeft());
    }
}
