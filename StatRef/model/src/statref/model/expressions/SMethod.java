package statref.model.expressions;

import statref.model.fragment.Place;
import statref.model.fragment.PlaceAdapter;
import statref.model.types.SType;

import java.util.stream.Stream;

public interface SMethod extends SCall {
    SExpression getQualifier();

    default void setQualifier(SExpression value) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    String getName();

    @Override
    default Stream<Place<SExpression>> getExpressions() {
        return Stream.concat(
                Stream.of(Qualifier.QUALIFIER),
                SCall.super.getExpressions());
    }

    class Qualifier implements PlaceAdapter<SMethod, SExpression> {
        public static final Place<SExpression> QUALIFIER = new Qualifier();

        @Override
        public String _getName(SMethod fragment) {
            return "t";
        }

        @Override
        public SType _getType(SMethod fragment) {
            SExpression qualifier = _get(fragment);
            if (qualifier == null) {
                // TODO check if it's this
                return null;
            }
            return qualifier.getType(); // TODO use declaration
        }

        @Override
        public SExpression _get(SMethod fragment) {
            // TODO qualifier must be this or link to a class if there is no qualifier
            // TODO but it's different from this
            // TODO should I add one more method?
            // TODO which one?
            // TODO resolve?
            // TODO it shouldn't be null, or should
            // TODO "null" expression is not null, so null means it's omitted and must be resolved from context
            // TODO but it's not for every fragment
            // TODO I need: 1 - way to tell that qulifier is omitted, 2 - resolve it on demand
            // TODO I can create default resolve implementation
            return fragment.getQualifier();
        }

        @Override
        public void _set(SMethod fragment, SExpression value) {
            fragment.setQualifier(value);
        }

        @Override
        public int hashCode() {
            return Qualifier.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Qualifier;
        }
    }
}
