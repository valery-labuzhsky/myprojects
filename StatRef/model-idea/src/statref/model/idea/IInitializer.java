package statref.model.idea;

import statref.model.SInitializer;

public interface IInitializer extends SInitializer, IVariableReference {
    @Override
    IExpression getInitializer();
}
