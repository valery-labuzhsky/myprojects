package statref.model.fragment;

import statref.model.expressions.SExpression;

import java.util.stream.Stream;

public interface Fragment {

    default Stream<Place<SExpression>> getExpressions() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    default Object getSignature() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
