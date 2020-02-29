package statref.model.builder.expressions;

import statref.model.SElement;
import statref.model.builder.statements.BMethodStatement;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
import statref.model.members.SMethodDeclaration;
import statref.model.statements.SStatement;
import statref.model.types.SType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BMethod extends BExpression implements SMethod {
    private SExpression qualifier;
    private final ArrayList<SExpression> params = new ArrayList<>();
    private SMethodDeclaration declaration;

    @SuppressWarnings("unused")
    public BMethod(SMethod method) {
        this(method.getQualifier(), method.findDeclaration());
        for (SExpression param : method.getParameters()) {
            parameter(param);
        }
    }

    public BMethod(SExpression qualifier, SMethodDeclaration declaration, SExpression... params) {
        if (declaration==null) {
            throw new NullPointerException("declaration");
        }
        this.qualifier = qualifier;
        this.declaration = declaration;
        this.params.addAll(Arrays.asList(params));
    }

    @Override
    public SExpression getQualifier() {
        return qualifier;
    }

    @Override
    public void setQualifier(SExpression qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public String getName() {
        return declaration.getName();
    }

    @Override
    public SType getType() {
        return findDeclaration().getReturnType();
    }

    @Override
    public List<SExpression> getParameters() {
        return params;
    }

    public BMethod parameter(SExpression param) {
        if (param==null) {
            throw new NullPointerException();
        }
        params.add(param);
        return this;
    }

    @Override
    public void setParameter(int index, SExpression expression) {
        if (expression==null) {
            throw new NullPointerException();
        }
        params.set(index, expression);
    }

    @Override
    public SMethodDeclaration findDeclaration() {
        return declaration;
    }

    @Override
    public SStatement toStatement() {
        return new BMethodStatement(this);
    }

    @Override
    public String toString() {
        String params = this.params.stream().map(SElement::getText).collect(Collectors.joining(", "));
        return (qualifier==null?"":qualifier+".")+getName()+"("+ params +")";
    }
}
