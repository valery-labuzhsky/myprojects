package statref.model.expressions;

import statref.model.fragment.Place;
import statref.model.fragment.PlaceAdapter;
import statref.model.members.SMethodDeclaration;
import statref.model.members.SParameter;
import statref.model.types.SType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface SMethod extends SExpression {
    SExpression getQualifier();

    default void setQualifier(SExpression value) {
        throw new UnsupportedOperationException();
    }

    String getName();

    List<? extends SExpression> getParameters();

    default Parameter getPlace(SParameter parameter) {
        return new Internal.ParameterInternal(parameter.getIndex());
    }

    static SMethod.Parameter getParameterPlace(int index) {
        return new Internal.ParameterInternal(index);
    }

    default void setParameter(int index, SExpression value) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    default SMethodDeclaration findDeclaration() {
        throw new UnsupportedOperationException();
    }

    @Override
    default List<Place<SExpression>> getExpressions() {
        List<Place<SExpression>> places = new ArrayList<>();
        places.add(Qualifier.QUALIFIER);
        for (int i = 0; i < getParameters().size(); i++) {
            places.add(new Internal.ParameterInternal(i));
        }
        return places;
    }

    @Override
    default boolean isStatement() {
        return true;
    }

    interface Parameter extends Place<SExpression> {
    }

    class Internal {
        private static class ParameterInternal implements Parameter, PlaceAdapter<SMethod, SExpression> {
            private final int index;

            public ParameterInternal(int index) {
                this.index = index;
            }

            @Override
            public String _getName(SMethod method) {
                return method.findDeclaration().getParameters().get(this.index).getName();
            }

            @Override
            public SType _getType(SMethod method) {
                SMethodDeclaration declaration = method.findDeclaration();
                if (declaration == null) {
                    return get(method).getType();
                } else {
                    // TODO I just need delegating it to the declaration fragment
                    return declaration.getParameters().get(this.index).getType();
                }
            }

            @Override
            public SExpression _get(SMethod fragment) {
                return fragment.getParameters().get(this.index);
            }

            @Override
            public void _set(SMethod fragment, SExpression value) {
                fragment.setParameter(this.index, value);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ParameterInternal that = (ParameterInternal) o;
                return index == that.index;
            }

            @Override
            public int hashCode() {
                return Objects.hash(index);
            }
        }
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
