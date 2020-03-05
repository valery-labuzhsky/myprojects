package streamline.plugin.refactoring.guts.flow;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;
import statref.model.idea.IInitializer;

import java.util.*;

public class Visitor {
    private final VariableFlow flow;
    private final LinkedHashSet<IInitializer> assignments = new LinkedHashSet<>();
    private HashMap<IElement, ArrayList<IInitializer>> values;
    private HashMap<IInitializer, ArrayList<IElement>> usages;

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
        System.out.println("Harvesting "+element); // TODO remove me
        if (flow.getAssignments().contains(element)) {
            System.out.println("Assignment"); // TODO remove me
            if (element instanceof IInitializer) {
                // TODO add declaration from the start - don't bother visiting it here
                assignments.add((IInitializer) element);
            } else {
                assignments.add((IInitializer) element.getParent());
            }
            return true;
        } else if (flow.getUsages().contains(element)) {
            System.out.println("Usage"); // TODO remove me
            values.put(element, new ArrayList<>(assignments));
            for (IInitializer assignment : assignments) {
                usages.computeIfAbsent(assignment, a -> new ArrayList<>()).add(element);
            }
            return false;
        }
        System.out.println("Not matched"); // TODO remove me
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

    public HashMap<IInitializer, ArrayList<IElement>> getUsages() {
        return usages;
    }
}
