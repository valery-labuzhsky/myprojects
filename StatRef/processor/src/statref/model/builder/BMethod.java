package statref.model.builder;

import statref.model.SExpression;
import statref.model.SMethod;

import java.util.Arrays;
import java.util.List;

public class BMethod extends BExpression implements SMethod {
    private final SExpression expression;
    private final String methodName;
    private final List<SExpression> params;

    public BMethod(SExpression expression, String methodName, SExpression... params) {
        this.expression = expression;
        this.methodName = methodName;
        this.params = Arrays.asList(params);
    }

    @Override
    public SExpression getExpression() {
        return expression;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public List<SExpression> getParams() {
        return params;
    }
}
