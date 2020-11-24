package statref.model.builder.expressions;

import statref.model.builder.statements.BMethodStatement;
import statref.model.expressions.SExpression;
import statref.model.expressions.SMethod;
import statref.model.members.SMethodDeclaration;
import statref.model.statements.SStatement;

import java.util.Arrays;

public class BMethod extends BCall implements SMethod {
    private SExpression qualifier;

    @SuppressWarnings("unused")
    public BMethod(SMethod method) {
        super(method);
        this.qualifier = method.getQualifier();
    }

    public BMethod(SExpression qualifier, SMethodDeclaration declaration, SExpression... params) {
        super(declaration);
        if (declaration==null) {
            throw new NullPointerException("declaration");
        }
        this.qualifier = qualifier;
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
    public SStatement toStatement() {
        return new BMethodStatement(this);
    }

    @Override
    public String toString() {
        return (qualifier == null ? "" : qualifier + ".") + super.toString();
    }

}
