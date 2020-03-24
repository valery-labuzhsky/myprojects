package streamline.plugin.refactoring.guts.flow;

public class LoopBlock extends Block {
    private final Block condition;
    private final Block body;

    public LoopBlock(Block condition, Block body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean harvest(Visitor visitor, Cycler cycler) {
        boolean override = condition.harvest(visitor, cycler);
        Visitor copy = visitor.copy();
        body.harvest(copy, cycler);
        body.harvest(copy, cycler);
        visitor.combine(copy);
        return override;
    }

}
