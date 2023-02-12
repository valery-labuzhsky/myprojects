package streamline.plugin.refactoring.guts.flow;

public class EmptyFlow extends ExecutionFlow {

    @Override
    public boolean harvest(Visitor visitor) {
        return false;
    }
}
