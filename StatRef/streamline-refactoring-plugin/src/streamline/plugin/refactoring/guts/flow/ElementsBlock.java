package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;

import java.util.ArrayList;
import java.util.List;

public class ElementsBlock extends Block {
    private final List<IElement> elements;

    public ElementsBlock(List<IElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, Cycler cycler) {
        for (int index = this.elements.size() - 1; index >= 0; index--) {
            if (cycler.visitElement(variants, elements.get(index))) return true;
        }
        return false;
    }

    @Override
    public Boolean getVariantsFrom(ArrayList<IInitializer> variants, Cycler cycler) {
        Boolean result = null;
        int index;
        for (index = this.elements.size() - 1; index >= 0; index--) {
            if (cycler.isBoundary(elements.get(index))) {
                index--;
                result = false;
                break;
            }
        }
        for (; index >= 0; index--) {
            if (cycler.visitElement(variants, elements.get(index))) return true;
        }
        return result;
    }

    @Override
    public boolean getVariantsTo(ArrayList<IInitializer> variants, Cycler cycler) {
        for (int index = this.elements.size() - 1; index >= 0; index--) {
            IElement element = elements.get(index);
            if (cycler.isBoundary(element)) {
                return cycler.visitBoundary(variants, element);
            } else {
                if (cycler.visitElement(variants, element)) return true;
            }
        }
        return false;
    }
}
