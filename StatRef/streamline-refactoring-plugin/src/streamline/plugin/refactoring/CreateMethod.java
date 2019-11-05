package streamline.plugin.refactoring;

import com.intellij.psi.PsiMethod;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.idea.IFactory;
import statref.model.idea.IMethodDeclaration;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

public class CreateMethod extends Refactoring {
    private final IMethodDeclaration after;
    private final BMethodDeclaration method;

    public CreateMethod(RefactoringRegistry registry, IMethodDeclaration after, BMethodDeclaration method) {
        super(registry);
        this.after = after;
        this.method = method;
    }

    public BMethodDeclaration getMethod() {
        return method;
    }

    @Override
    protected void doRefactor() {
        PsiMethod anchor = after.getElement();
        anchor.getParent().addAfter(IFactory.convertMethodDeclaration(method, after.getProject()).getElement(), anchor);
    }
}
