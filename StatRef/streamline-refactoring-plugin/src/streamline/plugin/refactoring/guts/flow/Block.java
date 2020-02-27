package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public abstract class Block {
    public abstract boolean getVariants(ArrayList<IInitializer> variants, Cycler cycler);

    public abstract Boolean getVariantsFrom(ArrayList<IInitializer> variants, Cycler cycler);

    public abstract boolean getVariantsTo(ArrayList<IInitializer> variants, Cycler cycler);
}
