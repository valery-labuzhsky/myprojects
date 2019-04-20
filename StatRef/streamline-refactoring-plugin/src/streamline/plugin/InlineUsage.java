package streamline.plugin;

import statref.model.idea.IElement;
import statref.model.idea.IVariable;

public class InlineUsage implements Refactoring {
    private final IVariable usage;
    private final AssignmentVariants variants = new AssignmentVariants();

    public InlineUsage(IVariable usage) {
        this.usage = usage;
    }

    public void add(IElement variant) {
        variants.add(variant);
    }

    public IVariable getUsage() {
        return usage;
    }

    public AssignmentVariants getVariants() {
        return variants;
    }
}
