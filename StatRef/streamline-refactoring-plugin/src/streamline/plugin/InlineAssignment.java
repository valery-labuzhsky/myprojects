package streamline.plugin;

import statref.model.idea.IVariable;

import java.util.ArrayList;

public class InlineAssignment implements Refactoring {
    private final IVariable variable;
    private final ArrayList<InlineUsage> usages = new ArrayList<>(); // TODO it's list of children refactorings

    public InlineAssignment(IVariable variable) {
        this.variable = variable;
    }

    public void add(InlineUsage usage) {
        usages.add(usage);
    }

    public IVariable getVariable() {
        return variable;
    }

    public ArrayList<InlineUsage> getUsages() {
        return usages;
    }
}
