package statref.model.expression;

import java.util.List;

public interface SMethod extends SExpression {
    SExpression getQualifier();

    String getName();

    List<? extends SExpression> getParams();
}
