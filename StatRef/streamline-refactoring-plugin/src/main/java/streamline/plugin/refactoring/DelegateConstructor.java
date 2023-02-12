package streamline.plugin.refactoring;

import statref.model.builder.members.BMethodDeclaration;
import statref.model.expressions.SNew;
import statref.model.fragment.ExpressionFragment;
import statref.model.members.SMethodDeclaration;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import javax.lang.model.element.Modifier;
import java.util.HashSet;

/**
 * Created on 24.11.2020.
 *
 * @author unicorn
 */
public class DelegateConstructor extends DelegateCall {
    public DelegateConstructor(RefactoringRegistry registry, ExpressionFragment fragment, HashSet<SMethodDeclaration.Signature> signatures) {
        super(registry, fragment, signatures);
    }

    @Override
    protected SNew getCall() {
        return (SNew) super.getCall();
    }

    @Override
    protected String getName() {
        return "new" + getCall().getClassReference().getSimpleName();
    }

    @Override
    protected BMethodDeclaration createDelegate(ExpressionFragment fragment) {
        BMethodDeclaration declaration = super.createDelegate(fragment);
        declaration.addModifier(Modifier.STATIC);
        declaration.returnType(getCall().getType());
        return declaration;
    }
}
