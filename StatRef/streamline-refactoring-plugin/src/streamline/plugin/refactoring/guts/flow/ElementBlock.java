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
    public boolean getVariants(ArrayList<IInitializer> variants, BoundaryCycler cycler) {
        return cycler.visitElement(variants, element);
    }

}
