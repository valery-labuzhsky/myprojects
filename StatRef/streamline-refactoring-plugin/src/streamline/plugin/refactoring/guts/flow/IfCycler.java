package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IIfStatement;

public class IfCycler extends BoundaryCycler {
    public IfCycler(AssignmentFlow flow, IIfStatement statement) {
        super(flow, new ParallelBlock().
                add(statement.getThenBranch()).
                add(statement.getElseBranch()));
    }
}
