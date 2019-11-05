package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import statref.model.expressions.SMethod;
import statref.model.fragment.ExpressionFragment;
import statref.model.idea.IMethod;
import statref.model.idea.IMethodDeclaration;
import statref.model.idea.IParameter;
import statref.model.members.SMethodDeclaration;
import streamline.plugin.nodes.guts.NodesRegistry;

import java.util.HashMap;
import java.util.HashSet;

public class InlineParameter extends CompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameter parameter;

    // TODO what next?
    // TODO make enter work
    // TODO do refactoring right away, make revert button
    // TODO I need to resolve these warning after all

    public InlineParameter(NodesRegistry registry, IParameter parameter) {
        super(registry.getRefactorings());
        this.parameter = parameter;

        IMethodDeclaration method = parameter.getParent();
        SMethod.Parameter parameterPlace = method.getPlace(parameter).getMethodPlace();

        HashSet<SMethodDeclaration.Signature> signatures = new HashSet<>();
        HashMap<Object, Delegate> delegates = new HashMap<>();
        for (IMethod call : method.getCalls()) {
            ExpressionFragment fragment = new ExpressionFragment(call).part(parameterPlace);
            delegates.computeIfAbsent(fragment.getSignature(), (s) -> {
                Delegate delegate = new Delegate(getRegistry(), fragment, signatures);
                add(delegate);
                return delegate;
            }).replace(fragment);
        }
    }

    @NotNull
    public IParameter getParameter() {
        return parameter;
    }

}
