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
        // TODO I need to use cycler here
        // TODO I can create a single stack and share it between cyclers, but it would mean I need to carefully maintain its state
        // TODO otherwise I can create multiple copies of it, which may have negative performance issues
        ArrayList<IInitializer> variants = new ArrayList<>();
        IElement context = usage;
        ElementStack boundaryStack = new ElementStack();
        do {
            // TODO this is the only thing to go to the cycler
            // TODO to do it rightfully I need body cycler separate from loop cycler and loop cycler must take advantage of body cycler
            // TODO so we have body cycle we cycle through it, go to parent, which is loop, it does header check then goes back to body, but only till the point of start
            // TODO this is an algorithm for going up, I must embrace it!
            IElement element = context;
            boundaryStack.add(element);
            context = element.getParent();
            if (new PointerCycler(this, context).startFrom(element).getVariants(variants)) break;
            // TODO now this last standing condition to remove
            // TODO all I need to do is to move it to another iteration
            if (isLoopBody(context)) { // TODO context becomes element
                // TODO what does element become?
                // TODO it just lost... may I restore it somehow?
                // TODO save it
                // TODO If only I had a true stack of usage contexts
                // TODO I probably need going all the way up, building my cyclers, then cycling them
                // TODO it will be start from element, I'll save it
                // TODO it I'm checking start element, I'll be cautious and pass it to a child one level down the stack
                // TODO it won't be universal anyway, so what's the point?
                // TODO just not to check for body loop and check for loop itself
                // TODO I need to remember this stop point
                new PointerCycler(this, context).upTo(boundaryStack).getVariants(variants);
            }
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
