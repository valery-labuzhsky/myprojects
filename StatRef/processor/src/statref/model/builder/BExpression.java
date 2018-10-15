package statref.model.builder;

import statref.model.SExpression;

public class BExpression implements SExpression {
    public BMethod call(String method, SExpression... params) {
        return new BMethod(this, method, params);
    }

    public BClassCast cast(BClass type) {
        return new BClassCast(this, type);
    }

    public BMethod call(String method) {
        return new BMethod(this, method);
    }

    public SExpression item(SExpression index) {
        return new BArrayItem(this, index);
    }

}
