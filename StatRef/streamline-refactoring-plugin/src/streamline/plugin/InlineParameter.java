package streamline.plugin;

import org.jetbrains.annotations.NotNull;
import statref.model.members.SMethodDeclaration;
import statref.model.builder.BFactory;
import statref.model.builder.expressions.BMethod;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
import statref.model.fragment.Fragment;
import statref.model.fragment.Place;
import statref.model.idea.*;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.refactoring.compound.CompoundRefactoring;

import java.util.*;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    // TODO brush me up
    // TODO what next?
    // TODO group refactoring tree
    // TODO create complex fragments
    // TODO make enter work
    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;

        IMethodDeclaration method = parameter.getParent(); // TODO take parameter as a place
        Map<Object, List<IMethod>> expressions = new HashMap<>();
        for (IMethod call : method.getCalls()) {
            IExpression expression = call.getExpression(parameter);
            expressions.computeIfAbsent(expression.getSignature(), (e) -> new ArrayList<>()).add(call);
        }

        SMethod.Parameter parameterPlace = method.getPlace(parameter).getMethodPlace();

        HashSet<SMethodDeclaration.Signature> signatures = new HashSet<>();
        for (List<IMethod> calls : expressions.values()) {
            Fragment fragment = BFactory.builder(calls.get(0).getExpression(parameter).fragment()); // TODO simplify
            String name = method.getName();
            BMethodDeclaration delegate = new BMethodDeclaration(name) {
                {
                    Fragment call = BFactory.builder(calls.get(0).fragment()); // TODO get rid of this special treatment - I gonna need complex fragments

                    for (Place<SExpression> methodPlace : call.getExpressions()) {
                        if (methodPlace.equals(parameterPlace)) {
                            for (Place<SExpression> fragmentPlace : fragment.getExpressions()) {
                                parameter(fragment, fragmentPlace);
                            }
                            methodPlace.set(call, (SExpression) fragment);
                        } else {
                            parameter(call, methodPlace);
                        }
                    }

                    return_((SExpression) call);
                }

                public void parameter(Fragment fragment, Place<SExpression> place) {
                    SExpression expression = place.get(fragment);
                    if (expression != null) {
                        place.set(fragment, parameter(place.getType(fragment), place.getName(fragment)));
                    }
                }
            };


            int suffix = 1;
            while (signatures.contains(delegate.getSignature())) {
                delegate.setName(name+suffix++);
            }
            signatures.add(delegate.getSignature());

            add(new CreateMethod(this.registry, method, delegate));

            for (IMethod methodCall : calls) {
                BMethod replacement = new BMethod(methodCall.getQualifier(), delegate.getName());
                Fragment call = methodCall.fragment();
                for (Place<SExpression> methodPlace : call.getExpressions()) {
                    if (methodPlace.equals(parameterPlace)) {
                        for (Place<SExpression> fragmentPlace : fragment.getExpressions()) {
                            addParameter(replacement, fragment, fragmentPlace);
                        }
                    } else {
                        addParameter(replacement, call, methodPlace);
                    }
                }

                add(new ReplaceElement(this.getRegistry(), methodCall, replacement));
            }
        }
    }

    private void addParameter(BMethod method, Fragment fragment, Place<SExpression> place) {
        SExpression expression = place.get(fragment);
        if (expression != null) {
            method.parameter(expression);
        }
    }

    @NotNull
    public IParameter getParameter() {
        return parameter;
    }

}
