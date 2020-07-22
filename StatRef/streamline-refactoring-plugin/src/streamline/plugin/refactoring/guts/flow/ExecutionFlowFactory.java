package streamline.plugin.refactoring.guts.flow;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;
import statref.model.idea.expressions.IConditional;
import statref.model.idea.statements.IIfStatement;
import statref.model.idea.statements.ILoopStatement;

public class ExecutionFlowFactory {
    private static ExecutionFlow loop(ILoopStatement statement) {
        return new LoopFlow(
                new EmptyFlow(),
                new ElementFlow(statement.getBody()));
    }

    private static ExecutionFlow ifFlow(IIfStatement statement) {
        return new SequenceFlow().
                add(statement.getCondition()).
                add(new ParallelFlow().
                        add(statement.getThenBranch()).
                        add(statement.getElseBranch()));
    }

    private static ExecutionFlow elements(IElement context) {
        return new ElementsFlow(context);
    }

    @NotNull
    private static ExecutionFlow conditional(IConditional element) {
        return new SequenceFlow().
                add(element.getCondition()).
                add(new ParallelFlow().
                        add(element.getThen()).
                        add(element.getElse()));
    }

    @NotNull
    public static ExecutionFlow flow(IElement element) {
        if (element instanceof IIfStatement) {
            return ifFlow((IIfStatement) element);
        } else if (element instanceof ILoopStatement) {
            return loop((ILoopStatement) element);
        } else if (element instanceof IConditional) {
            return conditional((IConditional) element);
        } else {
            return elements(element);
        }
    }
}
