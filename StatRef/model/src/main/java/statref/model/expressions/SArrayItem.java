package statref.model.expressions;

public interface SArrayItem extends SExpression {
    SExpression getExpression();

    SExpression getIndex();
}
