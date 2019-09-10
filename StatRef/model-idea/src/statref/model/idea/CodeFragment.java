package statref.model.idea;

import statref.model.SType;
import statref.model.expression.SExpression;

import java.util.function.BiConsumer;

// TODO move me to another package, module?
public abstract class CodeFragment {
    // TODO a fragment is not necessary an expression
    public abstract String getName(FragmentPlace e);

    public abstract SType getType(FragmentPlace place);

    public abstract SExpression get();

    public abstract SExpression get(FragmentPlace p);

    public abstract void set(FragmentPlace p, SExpression expression);

    public abstract void forEach(BiConsumer<CodeFragment, FragmentPlace> consumer);
}
