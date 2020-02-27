package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IInitializer;

import java.util.ArrayList;

public class LoopBlock extends Block {
    private final Block condition;
    private final Block body;

    public LoopBlock(Block condition, Block body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean getVariants(ArrayList<IInitializer> variants, Cycler cycler) {
        if (condition.getVariants(variants, cycler)) return true;
        body.getVariants(variants, cycler);
        return false;
    }

    @Override
    public Boolean getVariantsFrom(ArrayList<IInitializer> variants, Cycler cycler) {
        Boolean result = condition.getVariantsFrom(variants, cycler);
        if (result != null) {
            if (result) return true;
            body.getVariants(variants, cycler);
            return false;
        }
        result = body.getVariantsFrom(variants, cycler);
        if (result != null) {
            if (result) return true;
            if (condition.getVariants(variants, cycler)) return true;
            body.getVariantsTo(variants, cycler);
            return false;
        }
        return null;
    }

    @Override
    public boolean getVariantsTo(ArrayList<IInitializer> variants, Cycler cycler) {
        return false;
    }
}
