package statref.model.idea;

import statref.model.SInitializer;
import statref.model.idea.expressions.IExpression;
import statref.model.idea.expressions.IVariableReference;

public interface IInitializer extends SInitializer, IVariableReference {
    @Override
    IExpression getInitializer();
}
