package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;

import java.util.ArrayList;
import java.util.List;

public class ElementsBlock extends Block {
    private final List<IElement> elements;
    private int index;

    public ElementsBlock(List<IElement> elements) {
        this.elements = elements;
    }

    protected void goTo(IElement element) {
        int i = elements.indexOf(element);
        if (i >= 0) {
            index = i;
        } else {
            throw new RuntimeException("Element " + element + " is not found");
        }
    }

    public void gotoLast() {
        index = elements.size() - 1;
    }

    private boolean hasAny() {
        return index >= 0 && index < elements.size();
    }

    private void goUp() {
        index--;
    }

    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, BoundaryCycler cycler) {
        for (; hasAny(); goUp()) {
            if (cycler.visitElement(variants, elements.get(index))) return true;
        }
        return false;
    }
}
