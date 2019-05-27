package streamline.plugin.refactoring.assignment;

import statref.model.idea.IElement;
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
        for (IVariable usage : initializer.valueUsages()) {
            InlineUsage inlineUsage = new InlineUsage(usage);
            for (IElement variant : inlineUsage.getVariants()) {
                if (variant.equals(initializer)) {
                    inlineUsage.setSelected(variant); // TODO selection is not shown for some reason
                    usages.add(inlineUsage);
                }
            }
        }
        remove = new RemoveElement(initializer);
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

    public IInitializer getInitializer() {
        return initializer;
    }

    public ArrayList<InlineUsage> getUsages() {
        return usages;
    }

    public RemoveElement getRemove() {
        return remove;
    }
}
