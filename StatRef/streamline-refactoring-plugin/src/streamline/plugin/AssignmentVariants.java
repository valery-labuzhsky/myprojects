package streamline.plugin;

import statref.model.idea.IElement;

import java.util.ArrayList;

public class AssignmentVariants {
    private IElement selected;
    private final ArrayList<IElement> variants = new ArrayList<>();

    public void add(IElement variant) {
        variants.add(variant);
    }

    public IElement getSelected() {
        return selected;
    }

    public void setSelected(IElement variant) {
        this.selected = variant;
    }

    public ArrayList<IElement> getVariants() {
        return variants;
    }
}
