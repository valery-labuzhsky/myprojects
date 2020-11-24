package streamline.plugin.refactoring;

import statref.model.expressions.SMethod;
import statref.model.fragment.ExpressionFragment;
import statref.model.members.SMethodDeclaration;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.HashSet;

public class DelegateMethod extends DelegateCall {

    public DelegateMethod(RefactoringRegistry registry, ExpressionFragment fragment, HashSet<SMethodDeclaration.Signature> signatures) {
        super(registry, fragment, signatures);
    }

    @Override
    protected SMethod getCall() {
        return (SMethod) super.getCall();
    }

    @Override
    protected String getName() {
        return getCall().getName();
    }
}
