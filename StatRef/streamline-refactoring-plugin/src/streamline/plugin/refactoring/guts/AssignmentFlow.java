package streamline.plugin.refactoring.guts;

import statref.model.idea.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class AssignmentFlow {
    private final IElement top;
    private final HashMap<IElement, List<IElement>> variables = new HashMap<>();

    public AssignmentFlow(IVariable variable) {
        IElement declaration = variable.declaration();
        top = declaration.getParent().getParent(); // TODO may not work for every case
        add(declaration);
        for (IVariable usage : variable.mentions()) {
            if (usage.isAssignment()) {
                add(usage.getParent());
            }
        }
    }

    private void add(IElement assignment) {
        add(assignment.getParent(), assignment);
    }

    private void add(IElement context, IElement assignment) {
        if (context instanceof IIfStatement && conditional((IIfStatement) context, assignment)) {
            assignment = context;
        } else if (context instanceof ILoopStatement && conditional((ILoopStatement) context, assignment)) {
            assignment = context;
        } else {
            variables.computeIfAbsent(context, key -> new ArrayList<>()).add(assignment);
        }
        if (!context.equals(top)) {
            add(context.getParent(), assignment);
        }
    }

    private boolean conditional(ILoopStatement context, IElement assignment) {
        return context.getBody().contains(assignment);
    }

    private boolean conditional(IIfStatement context, IElement assignment) {
        return conditional(context.getThenBranch(), assignment) || conditional(context.getElseBranch(), assignment);
    }

    private boolean conditional(IStatement branch, IElement assignment) {
        return branch != null && branch.contains(assignment);
    }

    public ArrayList<IInitializer> getVariants(IElement usage) {
        ArrayList<IInitializer> variants = new ArrayList<>();
        IElement context = usage;
        do {
            context = context.getParent();
        } while (!getVariants(usage, context, variants) && !context.equals(top));
        return variants;
    }

    public boolean getVariants(IElement usage, IElement context, ArrayList<IInitializer> variants) {
        List<IElement> elements = variables.get(context);
        if (elements != null) {
            for (ListIterator<IElement> iterator = elements.listIterator(elements.size()); iterator.hasPrevious(); ) {
                IElement element = iterator.previous();
                if (usage == null || element.before(usage)) {
                    if (element instanceof IIfStatement) {
                        IIfStatement ifStatement = (IIfStatement) element;
                        if (getVariants(null, ifStatement.getElseBranch(), variants) &
                                getVariants(null, ifStatement.getThenBranch(), variants)) {
                            return true;
                        }
                    } else if (element instanceof ILoopStatement) {
                        getVariants(null, ((ILoopStatement) element).getBody(), variants);
                    } else {
                        variants.add((IInitializer) element);
                        return true;
                    }
                }
            }
            if (context instanceof ILoopStatement) {
                getVariants(null, ((ILoopStatement) context).getBody(), variants);
            }
       }
        return false;
    }
}
