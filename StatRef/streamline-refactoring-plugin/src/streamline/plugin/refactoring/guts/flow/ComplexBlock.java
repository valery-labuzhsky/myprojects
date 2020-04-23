package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

import java.util.ArrayList;
import java.util.List;

public abstract class ComplexBlock extends Block {
    protected final List<Block> blocks = new ArrayList<>();

    public ComplexBlock() {
    }

    public ComplexBlock add(Block block) {
        blocks.add(block);
        return this;
    }

    public ComplexBlock add(IElement element) {
        blocks.add(new ElementBlock(element));
        return this;
    }

}
