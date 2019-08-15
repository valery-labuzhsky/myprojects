package statref.model.builder;

import statref.model.expression.SExpression;
import statref.model.expression.SMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BMethod extends BExpression implements SMethod {
    private final SExpression qualifier;
    private final String name;
    private final List<SExpression> params;

    public BMethod(SExpression qualifier, String name, SExpression... params) {
        this.qualifier = qualifier;
        this.name = name;
        this.params = new ArrayList<>(Arrays.asList(params));
    }

    @Override
    public SExpression getQualifier() {
        return qualifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<SExpression> getParams() {
        return params;
    }

    public BMethod param(SExpression param) {
        params.add(param);
        return this;
    }
}
