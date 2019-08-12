package streamline.plugin;

import com.intellij.psi.PsiMethod;
import statref.model.idea.IMethodDeclaration;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;

public class CreateMethod extends Refactoring {
    private final IMethodDeclaration after;
    private final IMethodDeclaration method;

    public CreateMethod(RefactoringRegistry registry, IMethodDeclaration after, IMethodDeclaration method) {
        super(registry);
        this.after = after;
        this.method = method;
    }

    public IMethodDeclaration getMethod() {
        return method;
    }

    @Override
    protected void doRefactor() {
        PsiMethod anchor = after.getElement();
        anchor.getParent().addAfter(method.getElement(), anchor);
    }
}
