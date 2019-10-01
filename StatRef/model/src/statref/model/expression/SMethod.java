package statref.model.expression;

import statref.model.SMethodDeclaration;

import java.util.List;

public interface SMethod extends SExpression {
    SExpression getQualifier();

    String getName();

    List<? extends SExpression> getParameters();

    default SMethodDeclaration findDeclaration() {
        return null;
    }
}
