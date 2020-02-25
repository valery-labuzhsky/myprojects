package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public class EmptyBlock extends Block {
    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, BoundaryCycler cycler) {
        return false;
    }
}
