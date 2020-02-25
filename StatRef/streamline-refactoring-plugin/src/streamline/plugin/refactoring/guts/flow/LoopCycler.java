package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.ILoopStatement;

public class LoopCycler extends BoundaryCycler {
    public LoopCycler(AssignmentFlow flow, ILoopStatement statement) {
        super(flow, new ParallelBlock().
                add(new EmptyBlock()).
                add(statement.getBody())
        );
    }

}
