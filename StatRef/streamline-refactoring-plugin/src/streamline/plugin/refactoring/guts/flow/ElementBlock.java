package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;

import java.util.ArrayList;

public class ElementBlock extends Block {
    private final IElement element;

    public ElementBlock(IElement element) {
        this.element = element;
    }

    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, Cycler cycler) {
        return cycler.visitElement(variants, element);
    }

    @Override
    public Boolean getVariantsFrom(ArrayList<IInitializer> variants, Cycler cycler) {
        if (cycler.isBoundary(element)) {
            return false;
        }
        return null;
    }

    @Override
    public boolean getVariantsTo(ArrayList<IInitializer> variants, Cycler cycler) {
        if (cycler.isBoundary(element)) {
            return cycler.visitBoundary(variants, element);
        }
        return false;
    }

}
