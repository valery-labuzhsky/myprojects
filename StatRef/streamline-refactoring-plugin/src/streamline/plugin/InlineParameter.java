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
    private final IMethodDeclaration method;
    private final HashSet<SMethodDeclaration.Signature> signatures = new HashSet<>();

    // TODO brush me up
    // TODO what next?
    // TODO group refactoring tree
    // TODO make enter work
    // TODO choose method names
    // TODO do refactoring right away, make revert button
    // TODO I need to resolve these warning after all


    class Delegate {
        private final BMethodDeclaration delegate;
        private final List<ReplaceElement> replacements = new ArrayList<>();

        public Delegate(ExpressionFragment fragment) {
            this.delegate = chooseName(new BMethodDeclaration(method.getName()) {
                {
                    SExpression body = BFactory.builder(fragment);
                    for (Place<SExpression> methodPlace : body.getExpressions()) {
                        parameter(body, methodPlace);
                    }
                    return_(body);
                }

                public void parameter(Fragment fragment, Place<SExpression> place) {
                    SExpression expression = place.get(fragment);
                    if (expression != null) {
                        place.set(fragment, parameter(place.getType(fragment), place.getName(fragment)));
                    }
                }
            });
        }

        public void replace(IMethod call) {
            BMethod replacement = new BMethod(this.delegate.getName());
            for (Place<SExpression> callPlace : call.getExpressions()) {
                addParameter(replacement, call, callPlace);
            }
            replacements.add(new ReplaceElement(getRegistry(), call, replacement));
        }

        private BMethodDeclaration chooseName(BMethodDeclaration delegate) {
            int suffix = 1;
            while (signatures.contains(delegate.getSignature())) {
                delegate.setName(method.getName() + suffix++);
            }
            signatures.add(delegate.getSignature());
            return delegate;
        }

        private void addParameter(BMethod method, Fragment fragment, Place<SExpression> place) {
            SExpression expression = place.get(fragment);
            if (expression != null) {
                method.parameter(expression);
            }
        }

    }

    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;

        method = parameter.getParent();
        SMethod.Parameter parameterPlace = method.getPlace(parameter).getMethodPlace();

        HashMap<Object, Delegate> delegates = new HashMap<>();
        for (IMethod call : method.getCalls()) {
            ExpressionFragment fragment = new ExpressionFragment(call).part(parameterPlace);
            Object signature = fragment.getSignature();
            if (delegates.size()>0) {
                Object toCompareTo = delegates.keySet().iterator().next();
                signature.equals(toCompareTo); // TODO debug
            }
            delegates.computeIfAbsent(signature, (s) -> new Delegate(fragment)).replace(call);
        }

        for (Delegate delegate : delegates.values()) {
            add(new CreateMethod(this.getRegistry(), method, delegate.delegate));
            for (ReplaceElement replacement : delegate.replacements) {
                add(replacement);
            }
        }
    }

    @NotNull
    public IParameter getParameter() {
        return parameter;
    }

}
