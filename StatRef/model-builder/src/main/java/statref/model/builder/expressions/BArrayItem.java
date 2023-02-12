package statref.model.builder.expressions;

import statref.model.expressions.SArrayItem;
import statref.model.expressions.SExpression;

public class BArrayItem extends BExpression implements SArrayItem {
    private final BExpression expression;
    private final SExpression index;

    public BArrayItem(BExpression expression, SExpression index) {
        this.expression = expression;
        this.index = index;
    }

    @Override
    public SExpression getExpression() {
        return expression;
    }

    @Override
    public SExpression getIndex() {
        return index;
    }
}
