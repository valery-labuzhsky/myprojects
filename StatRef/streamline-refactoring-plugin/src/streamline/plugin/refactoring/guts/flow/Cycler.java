package streamline.plugin.refactoring.guts.flow;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.*;

import java.util.ArrayList;
import java.util.Collections;

public class Cycler {
    // TODO try to get rid of a state
    protected final Block block;
    private AssignmentFlow flow;
    private ElementStack boundary;
    private boolean startFrom;

    public Cycler(AssignmentFlow flow, Block block) {
        this.flow = flow; // TODO it is required to access to the flow I gathered using find usage
        this.block = block;
    }

    public static Cycler createLoopCycler(AssignmentFlow flow, ILoopStatement statement) {
        return new Cycler(flow, new LoopBlock(
                new EmptyBlock(),
                new ElementBlock(statement.getBody())));
    }

    public static Cycler createIfCycler(AssignmentFlow flow, IIfStatement statement) {
        return new Cycler(flow, new ParallelBlock().
                add(statement.getThenBranch()).
                add(statement.getElseBranch()));
    }

    public static Cycler createElementsCycler(AssignmentFlow flow, IElement context) {
        return new Cycler(flow, new ElementsBlock(flow.getVariables().getOrDefault(context, Collections.emptyList())));
    }

    public boolean visitElement(ArrayList<IInitializer> variants, IElement element) {
        if (harvest(variants, element)) {
            return true;
        } else if (worthVisiting(element)) {
            return createCycler(this.flow, element).getVariants(variants);
        }
        return false;
    }

    private boolean worthVisiting(IElement element) {
        return flow.getVariables().containsKey(element);
    }

    public boolean visitBoundary(ArrayList<IInitializer> variants, IElement element) {
        if (worthVisiting(element)) {
            Cycler cycler = createCycler(this.flow, element);
            this.boundary.down();
            try {
                cycler.upTo(this.boundary);
                return cycler.getVariants(variants);
            } finally {
                this.boundary.up();
            }
        }
        return false;
    }

    @NotNull
    public static Cycler createCycler(AssignmentFlow flow, IElement element) {
        if (element instanceof IIfStatement) {
            return createIfCycler(flow, (IIfStatement) element);
        } else if (element instanceof ILoopStatement) {
            return createLoopCycler(flow, (ILoopStatement) element);
        } else if (element instanceof IVariableDeclaration) {
            return createElementsCycler(flow, element);
        } else if (element instanceof IDeclarationStatement) {
            return createElementsCycler(flow, element);
        } else if (element instanceof IBlock) {
            return createElementsCycler(flow, element);
        } else if (element instanceof IBlockStatement) {
            return createElementsCycler(flow, element);
        } else if (element instanceof IExpressionStatement) {
            return createElementsCycler(flow, element);
        } else {
            throw new RuntimeException(element.getClass().getSimpleName() + " is not supported");
        }
    }

    public boolean isBoundary(IElement element) {
        return boundary != null && boundary.getTop().equals(element);
    }

    public Cycler upTo(ElementStack boundary) {
        this.boundary = boundary;
        return this;
    }

    public boolean getVariants(ArrayList<IInitializer> variants) {
        if (startFrom) {
            Boolean finish = block.getVariantsFrom(variants, this);
            if (finish==null) {
                throw new RuntimeException(boundary.getTop()+" is not found");
            }
            return finish;
        } else if (boundary != null) {
            return block.getVariantsTo(variants, this);
        } else {
            return block.getVariants(variants, this);
        }
    }

    private boolean harvest(ArrayList<IInitializer> variants, IElement element) {
        if (element instanceof IInitializer) { // TODO it's declaration, but it may be just usage
            variants.add((IInitializer) element);
            return true;
        } else if (element instanceof IVariable) { // TODO it can be declaration
            if (((IVariable) element).isAssignment()) { // TODO it doesn't mean there is no usage here
                variants.add((IInitializer) element.getParent());
                return true;
            }
        }
        return false;
    }

    public Cycler startFrom(ElementStack boundary) {
        this.boundary = boundary;
        startFrom = true;
        return this;
    }
}
