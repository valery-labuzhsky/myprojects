package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AssignmentFlow {
    private final IElement top;
    private final HashMap<IElement, List<IElement>> variables = new HashMap<>();

    public AssignmentFlow(IVariable variable) {
        this(variable.declaration());
    }

    private AssignmentFlow(IVariableDeclaration declaration) {
        top = declaration.getParent().getParent(); // TODO may not work for every case
        add(declaration);
        for (IVariable usage : declaration.mentions()) {
            add(usage); // TODO filter out only usable mentions?
        }
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

    public ArrayList<IInitializer> getVariants(IElement usage) {
        ArrayList<IInitializer> variants = new ArrayList<>();
        IElement context = usage;
        ElementStack boundaryStack = new ElementStack();
        do {
            IElement element = context;
            boundaryStack.add(element);
            context = element.getParent();
            if (Cycler.createCycler(this, context).startFrom(boundaryStack).getVariants(variants)) break;
        } while (!context.equals(top));
        Collections.reverse(variants);
        return variants;
    }

    // TODO now I must find usages based on assignments
    public ArrayList<IVariable> getUsages(IInitializer initializer) {

        // TODO how does previous algorithm work?
        // TODO starting from my element (context) iterate down
        // TODO if I meet usage - take
        // TODO if I meet assignment - break
        // TODO if I meet another context jump into
        // TODO if I reached end of my context, go to the parent context
        // TODO stupidity...
        // TODO it's so simple algorithm, why I cannot crack it?
        // TODO currently I'm starting from context of my variable which is good

        // TODO I'm thinking in terms of graph movements
        // TODO I also need implementing it this way to be understandable
        // TODO so I need some kind of iterator able to walk through my tree
        // TODO I can control it either by calling other methods or by returning some value

        // TODO return next
        // TODO return jump into
        // TODO return go up
        // TODO that's I must do

        // TODO how this walker works
        // TODO it somehow remembers current position
        // TODO it has context which it's walking through
        // TODO it has position in the context: either iterator or index
        return null;
    }
}
