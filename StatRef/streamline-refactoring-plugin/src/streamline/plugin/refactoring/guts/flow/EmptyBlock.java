package streamline.plugin.refactoring.guts.flow;

public class EmptyBlock extends Block {

    @Override
    public boolean harvest(Visitor visitor, Cycler cycler) {
        return false;
    }
}
