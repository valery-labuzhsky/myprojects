package streamline.plugin.refactoring.guts.flow;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.*;

import java.util.ArrayList;

public abstract class BoundaryCycler {
    protected final Block block;
    private AssignmentFlow flow;
    private ElementStack boundary;

    public BoundaryCycler(AssignmentFlow flow, Block block) {
        this.flow = flow; // TODO it is required to access to the flow I gathered using find usage
        this.block = block;
    }

    public boolean visitElement(ArrayList<IInitializer> variants, IElement element) {
        if (isBoundary(element)) {
            BoundaryCycler cycler = plunge(element);
            this.boundary.down();
            try {
                cycler.upTo(this.boundary);
                return cycler.getVariants(variants);
            } finally {
                this.boundary.up();
            }
        } else if (harvest(variants, element)) {
            return true;
        } else {
            return plunge(element).getVariants(variants);
        }
    }

    @NotNull
    private BoundaryCycler plunge(IElement element) {
        BoundaryCycler cycler;
        if (element instanceof IIfStatement) {
            cycler = new IfCycler(flow, (IIfStatement) element);
        } else if (element instanceof ILoopStatement) {
            cycler = new LoopCycler(flow, (ILoopStatement) element);
        } else {
            throw new RuntimeException(element.getClass().getSimpleName()+" is not supported");
        }
        return cycler;
    }

    private boolean isBoundary(IElement element) {
        return boundary != null && boundary.getTop().equals(element);
    }

    public BoundaryCycler upTo(ElementStack boundary) {
        this.boundary = boundary;
        return this;
    }

    public boolean getVariants(ArrayList<IInitializer> variants) {
        return block.getVariants(variants, this);
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

}
