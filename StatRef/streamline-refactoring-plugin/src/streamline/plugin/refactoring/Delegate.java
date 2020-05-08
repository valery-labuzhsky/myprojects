package streamline.plugin.refactoring;

import statref.model.builder.BFactory;
import statref.model.builder.expressions.BMethod;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
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

public class Delegate extends CompoundRefactoring {
    private final BMethodDeclaration delegate;
    private final ExpressionFragment fragment;
    private final CreateMethod create;
    private final List<ReplaceElement> replacements = new ArrayList<>();
    private HashSet<SMethodDeclaration.Signature> signatures;

    public Delegate(RefactoringRegistry registry, ExpressionFragment fragment, HashSet<SMethodDeclaration.Signature> signatures) {
        // TODO I could get rid of signature if did step by step refactoring
        super(registry, fragment.getBase());
        this.fragment = fragment;
        this.signatures = signatures;
        this.delegate = chooseName(new BMethodDeclaration(getName()) {
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
        create = add(new CreateMethod(this.getRegistry(), (IMethodDeclaration) getMethod().findDeclaration(), delegate));
    }

    private String getName() {
        return getMethod().getName();
    }

    private SMethod getMethod() {
        return (SMethod) fragment.getBase();
    }

    public CreateMethod getCreate() {
        return create;
    }

    public List<ReplaceElement> getReplacements() {
        return replacements;
    }

    public void replace(ExpressionFragment call) {
        BMethod replacement = new BMethod(null, delegate);
        for (Place<SExpression> callPlace : call.getExpressions()) {
            addParameter(replacement, call, callPlace);
        }
        replacements.add(add(new ReplaceElement(getRegistry(), (IElement) call.getBase(), replacement)));
    }

    private BMethodDeclaration chooseName(BMethodDeclaration delegate) {
        int suffix = 1;
        while (signatures.contains(delegate.getSignature())) {
            delegate.setName(getName() + suffix++);
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

    public String toString() {
        return "Delegate "+fragment;
    }

}
