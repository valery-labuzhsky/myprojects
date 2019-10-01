package statref.model.idea.expression;

import org.jetbrains.annotations.NotNull;
import statref.model.expression.SExpression;
import statref.model.idea.Fragment;
import statref.model.idea.Place;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleExpressionFragment extends Fragment {

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
    public SExpression get() {
        return expression;
    }

    @NotNull
    @Override
    public List<Place<SExpression>> getExpressions() {
        return Collections.emptyList();
    }
}
