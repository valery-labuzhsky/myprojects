package streamline.plugin;

import statref.model.idea.IElement;
import statref.model.idea.IVariable;

import java.util.ArrayList;

public class InlineAssignment implements Refactoring {
    private final IVariable variable;
    private final ArrayList<InlineUsage> usages = new ArrayList<>();

    public InlineAssignment(IVariable variable) {
        this.variable = variable;
        for (IVariable usage : variable.valueUsages()) {
            InlineUsage inlineUsage = new InlineUsage(usage);
            for (IElement variant : inlineUsage.getVariants()) {
                if (variant.equals(variable.getParent())) {
                    inlineUsage.setSelected(variant);
                    usages.add(inlineUsage);
                }
            }
        }
        // TODO add remove declaration here
    }

    @Override
    public void refactor() {
        for (InlineUsage usage : usages) {
            usage.refactor();
        }
    }

    public IVariable getVariable() {
        return variable;
    }

    public ArrayList<InlineUsage> getUsages() {
        return usages;
    }
}
