package statref.model.expression;

import java.util.List;

public interface SMethod extends SExpression {
    SExpression getCallee();

    String getMethodName();

    List<SExpression> getParams();
}
