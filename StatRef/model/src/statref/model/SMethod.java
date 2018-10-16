package statref.model;

import java.util.List;

public interface SMethod extends SExpression {
    SExpression getExpression();

    String getMethodName();

    List<SExpression> getParams();
}
