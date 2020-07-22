package streamline.plugin.refactoring.guts.flow;

public class LoopFlow extends ExecutionFlow {
    private final ExecutionFlow condition;
    private final ExecutionFlow body;

    public LoopFlow(ExecutionFlow condition, ExecutionFlow body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean harvest(Visitor visitor) {
        boolean override = condition.harvest(visitor);
        Visitor copy = visitor.copy();
        body.harvest(copy);
        body.harvest(copy);
        visitor.combine(copy);
        return override;
    }

}
