package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import statref.model.expressions.SMethod;
import statref.model.fragment.ExpressionFragment;
import statref.model.idea.IMethodDeclaration;
import statref.model.idea.IParameterDeclaration;
import statref.model.idea.expressions.ICall;
import statref.model.members.SMethodDeclaration;
import streamline.plugin.nodes.guts.NodesRegistry;

import java.util.HashMap;
import java.util.HashSet;

public class InlineParameter extends SimpleCompoundRefactoring {
    @org.jetbrains.annotations.NotNull
    private final IParameterDeclaration parameter;

    public InlineParameter(NodesRegistry registry, @NotNull IParameterDeclaration parameter) {
        super(registry.getRefactorings(), parameter);
        this.parameter = parameter;

        IMethodDeclaration method = parameter.getParent();
        boolean constructor = method.isConstructor();
        SMethod.Parameter parameterPlace = method.getPlace(parameter).getMethodPlace();

        HashSet<SMethodDeclaration.Signature> signatures = new HashSet<>();
        HashMap<Object, DelegateCall> delegates = new HashMap<>();
        for (ICall call : method.getCalls()) {
            ExpressionFragment fragment = new ExpressionFragment(call).part(parameterPlace);
            delegates.computeIfAbsent(fragment.getSignature(), (s) -> {
                DelegateCall delegate;
                // TODO I need remove signature out of this + make simple constructor
                if (constructor) {
                    delegate = new DelegateConstructor(getRegistry(), fragment, signatures);
                } else {
                    delegate = new DelegateMethod(getRegistry(), fragment, signatures);
                }
                add(delegate);
                return delegate;
            }).replace(fragment);
        }
    }

    @NotNull
    public IParameterDeclaration getParameter() {
        return parameter;
    }

}
