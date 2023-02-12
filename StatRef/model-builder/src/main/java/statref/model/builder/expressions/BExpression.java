package statref.model.builder.expressions;

import statref.model.builder.BElement;
import statref.model.builder.members.BMethodDeclaration;
import statref.model.expressions.SExpression;
import statref.model.members.SMethodDeclaration;
import statref.model.types.SClass;

public abstract class BExpression extends BElement implements SExpression {
    public BMethod call(String method, SExpression... params) {
        // TODO ideally I would need find the methon in the expression itself
        return new BMethod(this, new BMethodDeclaration(method), params);
    }

    public BMethod call(SMethodDeclaration method, SExpression... params) {
        return new BMethod(this, method, params);
    }

    public BClassCast cast(SClass type) {
        return new BClassCast(this, type);
    }

    public SExpression item(SExpression index) {
        return new BArrayItem(this, index);
    }

}
