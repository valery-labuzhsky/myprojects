package streamline.plugin.refactoring.guts.flow;

import org.assertj.core.util.Lists;
import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;

import java.util.*;

public class VariableFlow {
    private final IElement top;
    private final HashMap<IElement, List<IElement>> variables = new HashMap<>();
    private final HashSet<IVariable> assignments = new HashSet<>();
    private final HashSet<IVariable> usages = new HashSet<>();
    private final Visitor visitor = new Visitor(this);
    private IVariableDeclaration declaration;

    public VariableFlow(IVariable variable) {
        this(variable.declaration());
    }

    public VariableFlow(IVariableDeclaration declaration) {
        this.declaration = declaration;
        top = declaration.getParent().getParent(); // TODO may not work for every case
        add(declaration);
        for (IVariable mention : declaration.mentions()) {
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

    public IVariableDeclaration getDeclaration() {
        return declaration;
    }

    public HashSet<IVariable> getAssignments() {
        return assignments;
    }

    public HashSet<IVariable> getUsages() {
        return usages;
    }

    public ArrayList<IInitializer> getAssignments(IElement usage) {
        return visitor.getValues().getOrDefault(usage, new ArrayList<>());
    }

    public Collection<IVariable> getUsages(IInitializer initializer) {
        return visitor.getUsages().getOrDefault(initializer, Lists.emptyList());
    }
}
