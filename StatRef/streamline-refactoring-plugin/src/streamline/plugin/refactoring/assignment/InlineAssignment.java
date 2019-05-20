package streamline.plugin.refactoring.assignment;

import statref.model.idea.IElement;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.remove.RemoveElement;
import streamline.plugin.refactoring.usage.InlineUsage;

import java.util.ArrayList;

public class InlineAssignment extends Refactoring {
    private final IVariable variable;
    private final ArrayList<InlineUsage> usages = new ArrayList<>();
    private final RemoveElement remove;

    public InlineAssignment(IVariable variable) {
        this.variable = variable;
        for (IVariable usage : variable.valueUsages()) {
            InlineUsage inlineUsage = new InlineUsage(usage);
            for (IElement variant : inlineUsage.getVariants()) {
                if (variant.equals(variable.getParent())) {
                    inlineUsage.setSelected(variant); // TODO selection is not shown for some reason
                    usages.add(inlineUsage);
                }
            }
        }
        remove = new RemoveElement(variable.getParent());
        for (InlineUsage usage : usages) {
            if (!usage.isEnabled()) {
                remove.setEnabled(false);
            }
        }
    }

    @Override
    protected void doRefactor() {
        for (InlineUsage usage : usages) {
            usage.refactor();
        }
        remove.refactor();
    }

    public IVariable getVariable() {
        return variable;
    }

    public ArrayList<InlineUsage> getUsages() {
        return usages;
    }

    public RemoveElement getRemove() {
        return remove;
    }
}
