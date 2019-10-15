package statref.model.builder.expressions;

import statref.model.builder.BElement;
import statref.model.expressions.SExpression;
import statref.model.types.SClass;

public abstract class BExpression extends BElement implements SExpression {
    public BMethod call(String method, SExpression... params) {
        return new BMethod(this, method, params);
    }

    public BClassCast cast(SClass type) {
        return new BClassCast(this, type);
    }

    public BMethod call(String method) {
        return new BMethod(this, method);
    }

    public SExpression item(SExpression index) {
        return new BArrayItem(this, index);
    }

}
