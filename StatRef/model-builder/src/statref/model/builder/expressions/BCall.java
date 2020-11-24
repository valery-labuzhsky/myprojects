package statref.model.builder.expressions;

import statref.model.SElement;
import statref.model.expressions.SCall;
import statref.model.expressions.SExpression;
import statref.model.members.SMethodDeclaration;
import statref.model.types.SType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 24.11.2020.
 *
 * @author unicorn
 */
public abstract class BCall extends BExpression implements SCall {
    protected final ArrayList<SExpression> params = new ArrayList<>();
    protected SMethodDeclaration declaration;

    public BCall(SCall call) {
        this(call.findDeclaration());
        for (SExpression param : call.getParameters()) {
            parameter(param);
        }
    }

    public BCall(SMethodDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public List<SExpression> getParameters() {
        return params;
    }

    @Override
    public void setParameter(int index, SExpression expression) {
        if (expression == null) {
            throw new NullPointerException();
        }
        params.set(index, expression);
    }

    @Override
    public SMethodDeclaration findDeclaration() {
        return declaration;
    }

    public BCall parameter(SExpression param) {
        if (param == null) {
            throw new NullPointerException();
        }
        params.add(param);
        return this;
    }

    public String getName() {
        return declaration.getName();
    }

    @Override
    public SType getType() {
        return findDeclaration().getReturnType();
    }

    public String toString() {
        String params = this.params.stream().map(SElement::getText).collect(Collectors.joining(", "));
        return getName() + "(" + params + ")";
    }
}
