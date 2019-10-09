package statref.model.fragment;

import statref.model.expressions.SExpression;

import java.util.List;

public interface Fragment {

    default List<Place<SExpression>> getExpressions() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    default Object getSignature() {
        throw new UnsupportedOperationException();
    }
}
