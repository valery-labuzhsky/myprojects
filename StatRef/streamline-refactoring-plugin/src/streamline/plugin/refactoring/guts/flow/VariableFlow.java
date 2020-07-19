package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.ILocalVariable;
import statref.model.idea.ILocalVariableDeclaration;

import java.util.*;

public class VariableFlow {
    private final IElement top;
    private final HashMap<IElement, List<IElement>> variables = new HashMap<>();
    private final HashSet<ILocalVariable> assignments = new HashSet<>();
    private final HashSet<ILocalVariable> usages = new HashSet<>();
    private final Visitor visitor = new Visitor(this);
    private ILocalVariableDeclaration declaration;

    public VariableFlow(ILocalVariable variable) {
        this(variable.declaration());
    }

    public VariableFlow(ILocalVariableDeclaration declaration) {
        this.declaration = declaration;
        top = declaration.getParent().getParent(); // TODO may not work for every case
        add(declaration);
        for (ILocalVariable mention : declaration.mentions()) {
            if (mention.isAssignment()) {
                assignments.add(mention);
            } else {
                usages.add(mention);
            }
            add(mention);
        }

        Cycler.createCycler(top).harvest(visitor);
    }

    private void add(IElement element) {
        IElement context = element.getParent();
        List<IElement> elements = getVariables().get(context);
        if (elements == null) {
            elements = new ArrayList<>();
            elements.add(element);
            getVariables().put(context, elements);
        } else {
            IElement last = elements.get(elements.size() - 1);
            if (!last.equals(element)) {
                elements.add(element);
            } else {
                return;
            }
        }
        if (!context.equals(top)) {
            add(element.getParent());
        }
    }

    public HashMap<IElement, List<IElement>> getVariables() {
        return variables;
    }

    public ILocalVariableDeclaration getDeclaration() {
        return declaration;
    }

    public HashSet<ILocalVariable> getAssignments() {
        return assignments;
    }

    public HashSet<ILocalVariable> getUsages() {
        return usages;
    }

    public ArrayList<IInitializer> getAssignments(IElement usage) {
        return visitor.getValues().getOrDefault(usage, new ArrayList<>());
    }

    public Collection<ILocalVariable> getUsages(IInitializer initializer) {
        return visitor.getUsages().getOrDefault(initializer, Collections.emptyList());
    }
}
