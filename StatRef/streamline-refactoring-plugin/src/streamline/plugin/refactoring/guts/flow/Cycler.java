package streamline.plugin.refactoring.guts.flow;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.*;

// TODO it's just an empty shell now
public class Cycler {
    // TODO try to get rid of a state
    private final Block block;

    private Cycler(Block block) {
        this.block = block;
    }

    private static Cycler createLoopCycler(ILoopStatement statement) {
        return new Cycler(new LoopBlock(
                new EmptyBlock(),
                new ElementBlock(statement.getBody())));
    }

    private static Cycler createIfCycler(IIfStatement statement) {
        return new Cycler(new ParallelBlock().
                add(statement.getThenBranch()).
                add(statement.getElseBranch()));
    }

    private static Cycler createElementsCycler(IElement context) {
        return new Cycler(new ElementsBlock(context));
    }

    @NotNull
    public static Cycler createCycler(IElement element) {
        if (element instanceof IIfStatement) {
            return createIfCycler((IIfStatement) element);
        } else if (element instanceof ILoopStatement) {
            return createLoopCycler((ILoopStatement) element);
        } else if (element instanceof IVariableDeclaration) {
            return createElementsCycler(element);
        } else if (element instanceof IDeclarationStatement) {
            return createElementsCycler(element);
        } else if (element instanceof IBlock) {
            return createElementsCycler(element);
        } else if (element instanceof IBlockStatement) {
            return createElementsCycler(element);
        } else if (element instanceof IExpressionStatement) {
            return createElementsCycler(element);
        } else if (element instanceof IAssignment) { // TODO think about it
            return createElementsCycler(element);
        } else {
            throw new RuntimeException(element.getClass().getSimpleName() + " is not supported");
        }
    }

    public boolean harvest(Visitor visitor) {
        return block.harvest(visitor, this);
    }
}