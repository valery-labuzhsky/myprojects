package statref.model.builder;

import statref.model.SMethodDeclaration;
import statref.model.SType;
import statref.model.expression.SExpression;
import statref.model.expression.SMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BMethod extends BExpression implements SMethod {
    private SExpression qualifier;
    private String name;
    private final ArrayList<SExpression> params;
    private SMethodDeclaration declaration;

    public BMethod(SMethod method) {
        this(method.getQualifier(), method.getName());
        for (SExpression param : method.getParameters()) {
            parameter(param);
        }
        this.declaration = method.findDeclaration();
    }

    public BMethod(SExpression qualifier, String name, SExpression... params) {
        this.qualifier = qualifier;
        this.name = name;
        this.params = new ArrayList<>(Arrays.asList(params));
    }

    @Override
    public SExpression getQualifier() {
        return qualifier;
    }

    public void setQualifier(SExpression qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public String getName() {
        return name;
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
    public String toString() {
        String params = this.params.stream().map(Object::toString).collect(Collectors.joining(", "));
        return (qualifier==null?"":qualifier+".")+name+"("+ params +")";
    }
}
