package statref.model.idea.expression;

import statref.model.SType;
import statref.model.expression.SExpression;
import statref.model.idea.CodeFragment;
import statref.model.idea.FragmentPlace;

import java.util.Objects;
import java.util.function.BiConsumer;

public class SimpleExpressionFragment extends CodeFragment {

    private final SExpression expression;
    private final Object signature;

    public SimpleExpressionFragment(SExpression expression, Object signature) {
        this.expression = expression;
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleExpressionFragment that = (SimpleExpressionFragment) o;
        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature);
    }

    @Override
    public String getName(FragmentPlace e) {
        throw new IllegalArgumentException();
    }

    @Override
    public SType getType(FragmentPlace place) {
        throw new IllegalArgumentException();
    }

    @Override
    public SExpression get() {
        return expression;
    }

    @Override
    public SExpression get(FragmentPlace p) {
        throw new IllegalArgumentException();
    }

    @Override
    public void set(FragmentPlace p, SExpression expression) {
        throw new IllegalArgumentException();
    }

    @Override
    public void forEach(BiConsumer<CodeFragment, FragmentPlace> consumer) {
    }
}
