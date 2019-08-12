package statref.model.builder;

import statref.model.expression.SExpression;
import statref.model.expression.SMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BMethod extends BExpression implements SMethod {
    private final SExpression callee;
    private final String methodName;
    private final List<SExpression> params;

    public BMethod(SExpression callee, String methodName, SExpression... params) {
        this.callee = callee;
        this.methodName = methodName;
        this.params = new ArrayList<>(Arrays.asList(params));
    }

    @Override
    public SExpression getCallee() {
        return callee;
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
