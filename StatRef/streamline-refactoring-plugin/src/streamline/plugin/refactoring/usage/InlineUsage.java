package streamline.plugin.refactoring.usage;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.Refactoring;

import java.util.ArrayList;

public class InlineUsage extends Refactoring {
    private final IVariable usage;
    private IElement selected;
    private final ArrayList<IElement> variants = new ArrayList<>();

    public InlineUsage(IVariable usage) {
        this.usage = usage;
        this.variants.addAll(new AssignmentFlow(usage).getVariants(usage));
        if (variants.size() > 1) setEnabled(false);
    }

    @Override
    protected void doRefactor() {
        if (selected != null) {
            usage.replace(((IInitializer) selected).getInitializer());
        }
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
