package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public class ParallelBlock extends ComplexBlock {
    public ParallelBlock() {
    }

    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, BoundaryCycler cycler) {
        boolean finish = true;
        for (Block block : blocks) {
            finish &= block.getVariants(variants, cycler);
        }
        return finish;
    }
}
