package streamline.plugin.refactoring.assignment;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.remove.RemoveElement;
import streamline.plugin.refactoring.usage.InlineUsage;

import java.util.ArrayList;

public class InlineAssignment extends Refactoring {
    private final IInitializer initializer;
    private final ArrayList<InlineUsage> usages = new ArrayList<>();
    private final RemoveElement remove;

    public InlineAssignment(IInitializer initializer) {
        this.initializer = initializer;
        remove = new RemoveElement(initializer);
        for (IVariable usage : initializer.valueUsages()) {
            InlineUsage inlineUsage = new InlineUsage(usage);
            inlineUsage.setSelected(initializer);
            usages.add(inlineUsage);
        }
        tryRemove();
    }

    public void ensureEnabledNodes() {
        boolean empty = true;
        for (InlineUsage usage : getUsages()) {
            if (usage.isEnabled()) {
                empty = false;
            }
        }
        if (empty) {
            for (InlineUsage usage : getUsages()) {
                usage.setEnabled(true);
            }
        }
    }

    public boolean tryRemove() {
        return remove.setEnabled(!areUsagesLeft());
    }

    private boolean areUsagesLeft() {
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
        tryRemove();
    }
}
