package streamline.plugin.refactoring.guts.flow;

import statref.model.idea.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AssignmentFlow {
    // TODO we write down assignment for each context
    // TODO then we analyze which assignment goes first
    // TODO I may: 1. for every usage check if this usage belongs to given assignment
    // TODO 2. create a backward tree, it will be more efficient, but will duplicate the logic,
    // TODO so I must somehow get rid of duplication
    // TODO solving second task will probably help me later
    // TODO combining both of them and traversing back and forth will give me true VariableFlow
    // TODO combining all the variables gives me value flow
    // TODO so it worth solving anyway
    // TODO I can use it as is - just change an order
    // TODO what other tree I'm going to build?

    // TODO I need answering question: which usages use my value?
    // TODO how will I answer it?
    // TODO I'll go from every variable and see if I can reach my assignment
    // TODO another method is to go from an assignment down and see if I can reach a value
    // TODO so I need mixed assignment & usage in my list
    // TODO I cannot pass another assignment & I collect usages
    // TODO order of usages doesn't matter as far as they are not crossing assignments
    // TODO does it worth it?
    // TODO idea may have it implemented but, it cost time to find. there is high probability it won't fit, it's not fun

    // TODO yet another question, should I create a separate class for it?
    // TODO probably not, because I would like to cache it latter, the single tree will be better
    // TODO anyway it's just working copy, the true flow will consist of links between nodes
    // TODO this is just to find a true flow

    // TODO so little by little: 1. add usages here and make no regression, 2. implement finding variables

    // TODO what am I doing now?
    // TODO I need usages implemented
    // TODO => I need to make my algorithm working for both cases
    // TODO => I need convenient way to walking through tree
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
        getVariables().computeIfAbsent(context, key -> new ArrayList<>()).add(element);
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
            // TODO startFrom and upTo are connected
            // TODO how do I combine them?
            // TODO need cases
            // TODO pen and paper must do it for me

            // TODO so we start from some point, this point is on our boundaryStack
            // TODO if I go through a loop and I have stack I must go through my body once again to so I can check one iteration more
            // TODO where do I set this boundary?
            // TODO I set this boundary here
            // TODO startFrom must set boundary, but at the same time it should not invoke the code of reaching that boundary
            // TODO I set startFrom on a loop body
            // TODO I reach loop itself
            // TODO now body is on the stack, and I'm starting from body, but I'mm already went through it, I know it
            // TODO I shouldn't go to another body, I should not plunge into it
            // TODO I should complete a loop, and then start again the body, up to boundary
            // TODO therefore,
            // TODO 1. startFrom - must not plunge into it - merely invoke listener
            // TODO 2. startFrom must be present on every cycler/block
            // TODO 3. it's not necessary PointerCycler, I must be smart about it
            // TODO loop itself must understand all this boundary thing
            if (Cycler.createElementsCycler(this, context).startFrom(boundaryStack).getVariants(variants)) break;
        } while (!context.equals(top));
        Collections.reverse(variants);
        return variants;
    }

    private boolean isLoopBody(IElement element) {
        IElement loop = element.getParent();
        return loop instanceof ILoopStatement && ((ILoopStatement) loop).getBody().equals(element);
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
