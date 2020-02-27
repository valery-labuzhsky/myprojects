package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public class EmptyBlock extends Block {
    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, Cycler cycler) {
        return false;
    }

    @Override
    public Boolean getVariantsFrom(ArrayList<IInitializer> variants, Cycler cycler) {
        return null;
    }

    @Override
    public boolean getVariantsTo(ArrayList<IInitializer> variants, Cycler cycler) {
        return false;
    }
}
