package streamline.plugin;

import com.intellij.psi.PsiMethod;
import statref.model.members.SMethodDeclaration;
import statref.model.idea.IFactory;
import statref.model.idea.IMethodDeclaration;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;

public class CreateMethod extends Refactoring {
    private final IMethodDeclaration after;
    private final SMethodDeclaration method;

    public CreateMethod(RefactoringRegistry registry, IMethodDeclaration after, SMethodDeclaration method) {
        super(registry);
        this.after = after;
        this.method = method;
    }

    public SMethodDeclaration getMethod() {
        return method;
    }

    @Override
    protected void doRefactor() {
        PsiMethod anchor = after.getElement();
        anchor.getParent().addAfter(IFactory.convertMethodDeclaration(method, after.getProject()).getElement(), anchor);
    }
}
