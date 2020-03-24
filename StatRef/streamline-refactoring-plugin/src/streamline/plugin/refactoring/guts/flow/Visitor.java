package streamline.plugin.refactoring.guts.flow;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;

import java.util.*;

public class Visitor {
    private final VariableFlow flow;
    private final LinkedHashSet<IInitializer> assignments = new LinkedHashSet<>();
    private HashMap<IElement, ArrayList<IInitializer>> values;
    private HashMap<IInitializer, ArrayList<IVariable>> usages;

    public Visitor(VariableFlow flow) {
        this.flow = flow;
        this.values = new HashMap<>();
        this.usages = new HashMap<>();
    }

    private Visitor(Visitor original) {
        this.flow = original.flow;
        this.values = original.values;
        this.usages = original.usages;
        this.assignments.addAll(original.assignments);
    }

    public void override(Visitor visitor) {
        getAssignments().clear();
        combine(visitor);
    }

    public void combine(Visitor visitor) {
        getAssignments().addAll(visitor.getAssignments());
    }

    @NotNull
    public Visitor copy() {
        return new Visitor(this);
    }

    public boolean visit(IElement element) {
        if (harvest(element)) {
            return true;
        } else if (worthVisiting(element)) {
            return Cycler.createCycler(element).harvest(this);
        } else {
            return false;
        }
    }

    public List<IElement> getElements(IElement context) {
        return getFlow().getVariables().getOrDefault(context, Collections.emptyList());
    }

    public boolean worthVisiting(IElement element) {
        return getFlow().getVariables().containsKey(element);
    }

    public boolean harvest(IElement element) {
        if (flow.getDeclaration().equals(element)) {
            assignments.add((IInitializer) element);
            return true;
        } else if (flow.getAssignments().contains(element)) {
            assignments.add((IInitializer) element.getParent());
            return true;
        } else if (flow.getUsages().contains(element)) {
            values.put(element, new ArrayList<>(assignments));
            for (IInitializer assignment : assignments) {
                usages.computeIfAbsent(assignment, a -> new ArrayList<>()).add((IVariable) element);
            }
            return false;
        }
        return false;
    }

    public VariableFlow getFlow() {
        return flow;
    }

    public Set<IInitializer> getAssignments() {
        return assignments;
    }

    public HashMap<IElement, ArrayList<IInitializer>> getValues() {
        return values;
    }

    public HashMap<IInitializer, ArrayList<IVariable>> getUsages() {
        return usages;
    }
}
