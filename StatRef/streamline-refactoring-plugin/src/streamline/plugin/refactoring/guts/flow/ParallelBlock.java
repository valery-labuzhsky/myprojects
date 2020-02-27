package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public class ParallelBlock extends ComplexBlock {
    public ParallelBlock() {
    }

    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, Cycler cycler) {
        boolean finish = true;
        for (Block block : blocks) {
            finish &= block.getVariants(variants, cycler);
        }
        return finish;
    }

    @Override
    public Boolean getVariantsFrom(ArrayList<IInitializer> variants, Cycler cycler) {
        for (Block block : blocks) {
            Boolean result = block.getVariantsFrom(variants, cycler);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean getVariantsTo(ArrayList<IInitializer> variants, Cycler cycler) {
        boolean finish = true;
        for (Block block : blocks) {
            finish &= block.getVariantsTo(variants, cycler);
        }
        return finish;
    }
}
