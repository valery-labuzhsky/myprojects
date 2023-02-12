package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;

import java.util.ArrayList;
import java.util.List;

public abstract class ComplexFlow extends ExecutionFlow {
    protected final List<ExecutionFlow> flows = new ArrayList<>();

    public ComplexFlow() {
    }

    public ComplexFlow add(ExecutionFlow flow) {
        flows.add(flow);
        return this;
    }

    public ComplexFlow add(IElement element) {
        flows.add(new ElementFlow(element));
        return this;
    }

}
