package statref.model.idea;

import statref.model.SInitializer;
import statref.model.idea.expressions.IExpression;

public interface IInitializer extends SInitializer, IVariableReference {
    @Override
    IExpression getInitializer();
}
