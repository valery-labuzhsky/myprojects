package streamline.plugin;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;

import java.util.ArrayList;

public class InlineUsage implements Refactoring {
    private boolean enabled = true;
    private final IVariable usage;
    private IElement selected;
    private final ArrayList<IElement> variants = new ArrayList<>();

    public InlineUsage(IVariable usage) {
        this.usage = usage;
        this.variants.addAll(new AssignmentFlow(usage).getVariants(usage));
        if (variants.size() > 1) setEnabled(false);
    }

    @Override
    public void refactor() {
        if (enabled && selected!=null) {
            usage.replace(((IInitializer)selected).getInitializer());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public IVariable getUsage() {
        return usage;
    }

    public ArrayList<IElement> getVariants() {
        return variants;
    }

    public IElement getSelected() {
        return selected;
    }

    public void setSelected(IElement variant) {
        selected = variant;
    }
}
