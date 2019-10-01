package statref.model.idea;

import org.jetbrains.annotations.NotNull;
import statref.model.expression.SExpression;

import java.util.List;

// TODO move me to another package, module?
public abstract class Fragment {

    // TODO a fragment is not necessary an expression
    public abstract SExpression get();

    @NotNull
    public abstract List<Place<SExpression>> getExpressions();
}
