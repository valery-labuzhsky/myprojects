package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.ArrayList;
import java.util.Objects;

public class InlineUsage extends Refactoring {
    // TODO PsiExpressionList is not supported
    // TODO PsiLambdaExpression: is not supported
    // TODO java.lang.ClassCastException: class com.intellij.psi.impl.source.PsiFieldImpl cannot be cast to class com.intellij.psi.PsiLocalVariable (com.intellij.psi.impl.source.PsiFieldImpl and com.intellij.psi.PsiLocalVariable are in unnamed module of loader com.intellij.ide.plugins.cl.PluginClassLoader @205531ec)
    //	at statref.model.idea.IVariable.declaration(IVariable.java:37)
    //	at streamline.plugin.SLInlineAction.actionPerformed(SLInlineAction.java:52)
    //	at com.intellij.openapi.actionSystem.ex.ActionUtil.performActionDumbAware(ActionUtil.java:280)
    // TODO java.lang.Throwable: PsiConditionalExpression:color > 0 ? p : p.toLowerCase(): is not supported
    //	at com.intellij.openapi.diagnostic.Logger.error(Logger.java:146)
    //	at statref.model.idea.IFactory.getUnknownElement(IFactory.java:98)
    //	at statref.model.idea.IFactory.getElement(IFactory.java:91)
    //	at statref.model.idea.IElement.getElement(IElement.java:67)
    //	at statref.model.idea.IElement.getParent(IElement.java:36)
    //	at statref.model.idea.IVariable.isAssignment(IVariable.java:19)
    //	at streamline.plugin.refactoring.guts.flow.VariableFlow.<init>(VariableFlow.java:27)
    //	at streamline.plugin.refactoring.InlineVariable.<init>(InlineVariable.java:16)
    //	at streamline.plugin.SLInlineAction.actionPerformed(SLInlineAction.java:40)
    private final IVariable usage;
    private final IInitializer value;
    private final ArrayList<InlineUsage> variants = new ArrayList<>();

    public InlineUsage(RefactoringRegistry registry, IVariable usage, IInitializer value) {
        super(registry, usage);
        this.usage = usage;
        this.value = value;
        for (Refactoring refactoring : registry.getRefactorings(usage)) {
            if (refactoring instanceof InlineUsage) {
                InlineUsage sibling = (InlineUsage) refactoring;
                meetSibling(sibling);
                sibling.meetSibling(this);
            }
        }
    }

    private void meetSibling(InlineUsage sibling) {
        variants.add(sibling);
        sibling.onUpdate.invoke(() -> {
            if (sibling.isEnabled()) {
                setEnabled(false);
            }
        });
    }

    @Override
    protected void doRefactor() {
        usage.replace(value.getInitializer());
    }

    public IVariable getUsage() {
        return usage;
    }

    public ArrayList<InlineUsage> getVariants() {
        return variants;
    }

    public IInitializer getValue() {
        return value;
    }

    @Override
    public void enableAll() {
        if (variants.isEmpty()) setEnabled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineUsage usage = (InlineUsage) o;
        return this.usage.equals(usage.usage) &&
                value.equals(usage.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usage, value);
    }

    @Override
    public String toString() {
        return "Replace " + usage.getText() + " with " + value.getText();
    }
}
