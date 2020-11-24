package streamline.plugin.refactoring;

import statref.model.builder.BFactory;
import statref.model.builder.expressions.BMethod;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.expressions.SCall;
import statref.model.expressions.SExpression;
import statref.model.fragment.ExpressionFragment;
import statref.model.fragment.Fragment;
import statref.model.fragment.Place;
import statref.model.idea.IElement;
import statref.model.idea.IMethodDeclaration;
import statref.model.members.SMethodDeclaration;
import streamline.plugin.refactoring.guts.RefactoringRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created on 24.11.2020.
 *
 * @author unicorn
 */
public abstract class DelegateCall extends SimpleCompoundRefactoring {
    private final List<ReplaceElement> replacements = new ArrayList<>();
    private final BMethodDeclaration delegate;
    private final ExpressionFragment fragment;
    private final CreateMethod create;
    private final HashSet<SMethodDeclaration.Signature> signatures;

    public DelegateCall(RefactoringRegistry registry, ExpressionFragment fragment, HashSet<SMethodDeclaration.Signature> signatures) {
        // TODO I could get rid of signature if did step by step refactoring
        super(registry, fragment.getBase());
        this.fragment = fragment;
        this.signatures = signatures;
        delegate = createDelegate(fragment);
        create = add(new CreateMethod(this.getRegistry(), (IMethodDeclaration) getCall().findDeclaration(), delegate));
    }

    public void replace(ExpressionFragment call) {
        BMethod replacement = createReplacement(call);
        replacements.add(add(new ReplaceElement(getRegistry(), (IElement) call.getBase(), replacement)));
    }

    private BMethod createReplacement(ExpressionFragment call) {
        BMethod replacement = new BMethod(null, delegate);
        call.getExpressions().forEachOrdered(callPlace -> addParameter(replacement, call, callPlace));
        return replacement;
    }

    private void addParameter(BMethod method, Fragment fragment, Place<SExpression> place) {
        SExpression expression = place.get(fragment);
        if (expression != null) {
            method.parameter(expression);
        }
    }

    public String toString() {
        return "Delegate " + fragment;
    }

    protected BMethodDeclaration createDelegate(ExpressionFragment fragment) {
        return chooseName(new BMethodDeclaration(getName()) {
            {
                SExpression body = BFactory.builder(fragment);
                body.getExpressions().forEachOrdered(methodPlace -> parameter(body, methodPlace));
                return_(body);
            }

            public void parameter(Fragment f, Place<SExpression> p) {
                SExpression expression = p.get(f);
                if (expression != null) {
                    p.set(f, parameter(p.getType(f), p.getName(f)));
                }
            }
        });
    }

    protected abstract String getName();

    protected SCall getCall() {
        return (SCall) fragment.getBase();
    }

    public CreateMethod getCreate() {
        return create;
    }

    public List<ReplaceElement> getReplacements() {
        return replacements;
    }

    private BMethodDeclaration chooseName(BMethodDeclaration delegate) {
        int suffix = 1;
        while (signatures.contains(delegate.getSignature())) {
            delegate.setName(getName() + suffix++);
        }
        signatures.add(delegate.getSignature());
        return delegate;
    }
}
