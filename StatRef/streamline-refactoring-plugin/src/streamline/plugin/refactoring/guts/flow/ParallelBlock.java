package streamline.plugin.refactoring.guts.flow;

public class ParallelBlock extends ComplexBlock {
    public ParallelBlock() {
    }

    @Override
    public boolean harvest(Visitor visitor, Cycler cycler) {
        boolean override = true;
        Visitor combined = visitor.copy();
        for (Block block : blocks) {
            Visitor blockVisitor = visitor.copy();
            override &= block.harvest(blockVisitor, cycler);
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
