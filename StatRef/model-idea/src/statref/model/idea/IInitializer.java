package statref.model.idea;

import statref.model.SInitializer;

public interface IInitializer extends SInitializer {
    @Override
    IExpression getInitializer();
}
