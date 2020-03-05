package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.IElement;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class VariableFlow {
    private final IElement top;
    private final HashMap<IElement, List<IElement>> variables = new HashMap<>();
    private final HashSet<IElement> assignments = new HashSet<>();
    private final HashSet<IElement> usages = new HashSet<>();

    public VariableFlow(IVariable variable) {
        this(variable.declaration());
    }

    private VariableFlow(IVariableDeclaration declaration) {
        top = declaration.getParent().getParent(); // TODO may not work for every case
        add(declaration);
        assignments.add(declaration);
        for (IVariable mention : declaration.mentions()) {
            if (mention.isAssignment()) {
                assignments.add(mention);
            } else {
                usages.add(mention);
            }
            add(mention);
        }

        // TODO I can calculate both instead of separate
        // TODO how will I do it?
        // TODO go step by step
        // TODO each element returns their own possibilities, they are combined together
        // TODO I need changing my blocks
        // TODO when I go visitor collects all the needed elements
        // TODO I pass the elements I have there, then I combine them together

        // TODO 1. I have some possible value on entering cycle
        // TODO 2. If some values are overriden in condition I must take them

        // TODO Basically, I enter with empty values
        // TODO I exit with possible values + whether or not I should check something else
        // TODO If I need to replace them, I replace them
        // TODO If I don't I add all the values from before a loop + all the values in the loop
        // TODO something like that
        // TODO I'd like to copy all the values, I can pass them to the usage variants

//        Cycler.createCycler(top).getAllVariants(new Visitor(this));
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

    public HashSet<IElement> getAssignments() {
        return assignments;
    }

    public HashSet<IElement> getUsages() {
        return usages;
    }

    public ArrayList<IInitializer> getAssignments(IElement usage) {
//        Visitor visitor = new Visitor(this);
//        IElement context = usage;
//        ElementStack boundaryStack = new ElementStack();
//        do {
//            IElement element = context;
//            boundaryStack.add(element);
//            context = element.getParent();
//            if (Cycler.createCycler(context).startFrom(boundaryStack).getVariants(visitor)) break;
//        } while (!context.equals(top));
//        Set<IInitializer> assignments = visitor.getAssignments();
//        ArrayList<IInitializer> list = new ArrayList<>(assignments);
//        Collections.reverse(list);
//        return list;
        // TODO now I need use this built assignments to gather usage variants instead of this complex algorithm
        Visitor visitor = new Visitor(this);
        Cycler.createCycler(top).harvest(visitor);
        return visitor.getValues().getOrDefault(usage, new ArrayList<>());
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
