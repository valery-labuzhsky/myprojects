package streamline.plugin;

import org.jetbrains.annotations.NotNull;
import statref.model.builder.BMethod;
import statref.model.builder.BMethodDeclaration;
import statref.model.builder.BParameter;
import statref.model.builder.BVariable;
import statref.model.idea.*;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.refactoring.compound.CompoundRefactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;
        IMethodDeclaration method = parameter.getParent();
        // TODO I need to find
        ArrayList<IMethod> calls = method.getCalls();
        Map<CodeFragment, List<CodeFragment>> expressions = new HashMap<>();
        for (IMethod call : calls) {
            // TODO first I need to create fragment for method call
            // TODO then I need to replace one of parameters with another fragment
            // TODO this expression is yet another parameter
            // TODO do I need to create hierarchy? probably yes
            // TODO it should be more semantic then syntax

            // TODO for method fragment I need Method delcaration + all the parameters
            // TODO basically I don't need method at this point, just parameter
            // TODO basically builders will serve well at this point
            // TODO all we need to do is to provide an interface
            // TODO do we need an instance, probably yes, or maybe not
            // TODO we will replace expression as a whole at this point, and at every point in fact

            // TODO so it will be yet another parallel hierarchy :-(

            IExpression expression = call.getExpression(parameter);
            CodeFragment callFragment = call.fragment();
            CodeFragment parameterFragment = expression.fragment();
            // TODO now I need replace
            callFragment = callFragment.input(expression, parameterFragment); // TODO what about this parameter? how can I identify it?
            // TODO it's kind of easy with parameters, but not really when we have 2 functions
            // TODO I can identify them with expressions for fragment instances, not so easy with just fragments
            // TODO how can I do it?
            // TODO do I need abstract identification at all?
            expressions.computeIfAbsent(callFragment, (k) -> new ArrayList<>()).add(callFragment);
            // TODO I need fragment for a method to put it as a value
        }

        // TODO inline value

        // TODO I need to think through Signature:
        // TODO it will have:
        // TODO input parameters
        // TODO output parameters
        // TODO some code inside
        // TODO much like a method

        // TODO then I create method with this signature, add parameters to it, replace every signature with code invocation
        // TODO so basically I need creating method for a signature
        // TODO signature must include method call as well
        // TODO my signature will be: method call + parameter signature
        // TODO let's do the signature
        // TODO I need creating class for it
        // TODO then I need using it
        // TODO simple
        // TODO let's do parameter signature fisrt

        int suffix = 0;
        for (Map.Entry<CodeFragment, List<CodeFragment>> entry : expressions.entrySet()) {
            CodeFragment value = entry.getKey();
            // TODO create a method with code fragment
            CodeFragment fragment = entry.getKey();
            // TODO propose a name for a method
            BMethodDeclaration prototype = new BMethodDeclaration(method.getName() + suffix++) {
                @Override
                public void describe() {
                    List<BParameter> inputs = fragment.inputs();
                    BParameter thisInput = fragment.thisInput();
                    CodeFragment delegate = fragment.copy();
                    // TODO how would I figure out which parameter is actual method?
                    // TODO I need to track it all along
                    // TODO moreover I need to choose the class
                    // TODO one of the parameters will be this
                    // TODO this parameter is which the method is called upon
                    // TODO do I need tracking it? and how?
                    // TODO actually it will be the first parameter
                    // TODO I don't like this definition
                    // TODO better one: parameter which is called the method
                    // TODO how would I find it?
                    // TODO I believe I need to know the structure well enough anyway
                    // TODO but how?
                    // TODO output.thisInput()
                    // TODO output.paramInputs()
                    for (BParameter input : inputs) {
                        if (!input.equals(thisInput)) {
                            // TODO except for one parameter which is the class to add this method to!
                            delegate.input(input, param(input)); // TODO add parameters we'd like to have names for our outputs
                        }
                    }

                    delegate.code(this);

                    // TODO I need creating method with the fragment

                    // TODO I need to specify parameters here
                    // TODO It's not a method but rather code ~expression
                    // TODO how I can pass parameters here?
                    // TODO create method should be above, the is the body
                    // TODO I need to say that this will be not static method
                    // TODO I need to create another fragment where outputs will be wired to parameters

/*
                    BMethod delegate = new BMethod(null, method.getName());
                    for (IParameter param : method.getParameters()) {
                        if (param.equals(parameter)) {
                            // TODO it won't work with complex usages
                            // TODO we need using signature
                            // TODO it's very important it will allow doing other staff easily
                            delegate.param(value.getExpression()); // TODO I can actually end up in something that cannot be turn into expression, so I must write down many lines code
                        } else {
                            delegate.param(new BVariable(param));
                        }
                    }
                    if (method.isVoid()) {
                        code(delegate);
                    } else {
                        return_(delegate);
                    }
*/
                }
            }.returnType(method.getReturnType());

            IMethodDeclaration newMethod = IFactory.convertMethodDeclaration(prototype, method.getProject());
            CreateMethod createMethod = new CreateMethod(this.registry, method, newMethod); // TODO the class is implied here!
            add(createMethod);

            int index = method.getParameterIndex(parameter);

            // TODO I need to apply signature here
            // TODO what does it mean
            // TODO use signature for all of that
            // TODO signature has inputs and output (sutable for replacement with a method)
            // TODO applying signature means:
            // TODO 1 - find corresponding code fragment,
            // TODO 2 - find expression for every input
            // TODO there is another structure - signature instance which corresponds to it
            // TODO next I replace one signature with another
            for (IMethod call : entry.getValue()) {
                BMethod delegate = new BMethod(call.getQualifier(), prototype.getName());
                List<IExpression> params = call.getParams();
                for (int i = 0; i < params.size(); i++) {
                    IExpression param = params.get(i);
                    if (index!=i) {
                        delegate.param(param);
                    }
                }
                // TODO replace
            }


        }
    }

    @NotNull
    public IParameter getParameter() {
        return parameter;
    }
}
