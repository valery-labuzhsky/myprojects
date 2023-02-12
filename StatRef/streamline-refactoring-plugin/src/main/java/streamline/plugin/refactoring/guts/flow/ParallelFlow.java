package streamline.plugin.refactoring.guts.flow;

public class ParallelFlow extends ComplexFlow {
    public ParallelFlow() {
    }

    @Override
    public boolean harvest(Visitor visitor) {
        boolean override = true;
        Visitor combined = visitor.copy();
        for (ExecutionFlow flow : flows) {
            Visitor blockVisitor = visitor.copy();
            override &= flow.harvest(blockVisitor);
            combined.getAssignments().addAll(blockVisitor.getAssignments());
        }
        if (override) {
            visitor.override(combined);
        } else {
            visitor.combine(combined);
        }
        return override;
    }

}
