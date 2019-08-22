package statref.model.idea.expression;

import statref.model.expression.SExpression;
import statref.model.idea.CodeFragment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SimpleExpressionFragment extends CodeFragment {

    private final SExpression expression;
    private final Object signature;

    public SimpleExpressionFragment(SExpression expression, Object signature) {
        this.expression = expression;
        this.signature = signature;
    }

    @Override
    public SExpression getExpression(Function<SExpression, SExpression> function) {
        return expression;
    }

    @Override
    public List<SExpression> getInputs() {
        return Collections.emptyList();
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
}
