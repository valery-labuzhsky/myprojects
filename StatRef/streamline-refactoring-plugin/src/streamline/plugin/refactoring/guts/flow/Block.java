package streamline.plugin.refactoring.guts.flow;

public abstract class Block {
    public abstract boolean harvest(Visitor visitor, Cycler cycler);
}
