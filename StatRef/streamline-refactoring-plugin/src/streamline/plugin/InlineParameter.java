package streamline.plugin;

import org.jetbrains.annotations.NotNull;
import statref.model.builder.BFactory;
import statref.model.builder.expressions.BMethod;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
import statref.model.fragment.ExpressionFragment;
import statref.model.fragment.Fragment;
import statref.model.fragment.Place;
import statref.model.idea.IElement;
import statref.model.idea.IMethod;
import statref.model.idea.IMethodDeclaration;
import statref.model.idea.IParameter;
import statref.model.members.SMethodDeclaration;
import streamline.plugin.nodes.NodesRegistry;
import streamline.plugin.refactoring.compound.CompoundRefactoring;

import java.util.*;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    // TODO brush me up
    // TODO what next?
    // TODO group refactoring tree
    // TODO make enter work
    // TODO choose method names
    // TODO do refactoring right away, make revert button
    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;

        IMethodDeclaration method = parameter.getParent();
        SMethod.Parameter parameterPlace = method.getPlace(parameter).getMethodPlace();

        Map<Object, List<ExpressionFragment>> expressions = new HashMap<>();
        for (IMethod call : method.getCalls()) {
            SExpression expression = parameterPlace.get(call);
            expressions.computeIfAbsent(expression.getSignature(), (e) -> new ArrayList<>()).add(new ExpressionFragment(call).part(parameterPlace));
        }


        HashSet<SMethodDeclaration.Signature> signatures = new HashSet<>();
        for (List<ExpressionFragment> calls : expressions.values()) {
            String name = method.getName();
            BMethodDeclaration delegate = new BMethodDeclaration(name) {
                {
                    SExpression call = BFactory.builder(calls.get(0));
                    for (Place<SExpression> methodPlace : call.getExpressions()) {
                        parameter(call, methodPlace);
                    }
                    return_(call);
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
                delegate.setName(name + suffix++);
            }
            signatures.add(delegate.getSignature());

            add(new CreateMethod(this.registry, method, delegate));

            for (ExpressionFragment call : calls) {
                BMethod replacement = new BMethod(delegate.getName());
                for (Place<SExpression> callPlace : call.getExpressions()) {
                    addParameter(replacement, call, callPlace);
                }

                add(new ReplaceElement(this.getRegistry(), (IElement) call.getBase(), replacement));
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
