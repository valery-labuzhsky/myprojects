package streamline.plugin;

import org.jetbrains.annotations.NotNull;
import statref.model.SMethodDeclaration;
import statref.model.builder.BMethod;
import statref.model.builder.BMethodDeclaration;
import statref.model.expression.SExpression;
import statref.model.idea.*;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.refactoring.compound.CompoundRefactoring;

import java.util.*;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    // TODO brush me up
    // TODO what next?
    // TODO rename methods
    // TODO group refactoring tree
    // TODO create complex fragments
    // TODO make elements be fragments
    // TODO make enter work
    // TODO resolve warnings
    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;

        IMethodDeclaration method = parameter.getParent();
        ArrayList<IMethod> calls = method.getCalls();
        Map<Fragment, List<IMethod>> expressions = new HashMap<>();
        for (IMethod call : calls) {
            IExpression expression = call.getExpression(parameter);
            expressions.computeIfAbsent(expression.fragment(), (e) -> new ArrayList<>()).add(call);
        }

        IMethod.ParameterPlace parameterPlace = method.getPlace(parameter).getMethodPlace();

        HashSet<SMethodDeclaration.Signature> signatures = new HashSet<>();
        for (Map.Entry<Fragment, List<IMethod>> entry : expressions.entrySet()) {
            Fragment fragment = entry.getKey();
            String name = method.getName();
            BMethodDeclaration delegate = new BMethodDeclaration(name) {
                {
                    Fragment call = entry.getValue().get(0).fragment(); // TODO get rid of this special treatment - I gonna need complex fragments

                    for (Place<SExpression> methodPlace : call.getExpressions()) {
                        if (methodPlace.equals(parameterPlace)) {
                            for (Place<SExpression> fragmentPlace : fragment.getExpressions()) {
                                parameter(fragment, fragmentPlace);
                            }
                            methodPlace.set(call, fragment.get());
                        } else {
                            parameter(call, methodPlace);
                        }
                    }

                    SExpression target = call.get();

                    if (target.getType().isClass(void.class)) {
                        code((BMethod) target);
                    } else {
                        return_(target);
                    }
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

            for (IMethod methodCall : entry.getValue()) {
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
