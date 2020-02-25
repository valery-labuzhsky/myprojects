package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public abstract class Block {
    public abstract boolean getVariants(ArrayList<IInitializer> variants, BoundaryCycler cycler);
}
