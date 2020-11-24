package statref.model.expressions;

import statref.model.fragment.Place;
import statref.model.fragment.PlaceAdapter;
import statref.model.members.SMethodDeclaration;
import statref.model.members.SParameter;
import statref.model.types.SType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 23.11.2020.
 *
 * @author unicorn
 */
public interface SCall extends SExpression {
    static Parameter getParameterPlace(int index) {
        return new Internal.ParameterInternal(index);
    }

    default SMethodDeclaration findDeclaration() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object getSignature() {
        return findDeclaration().getSignature();
    }

    List<? extends SExpression> getParameters();

    default void setParameter(int index, SExpression value) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    default Parameter getPlace(SParameter parameter) {
        return new Internal.ParameterInternal(parameter.getIndex());
    }

    @Override
    default Stream<Place<SExpression>> getExpressions() {
        return Stream.iterate(0, i -> i + 1).limit(getParameters().size()).map(i -> new Internal.ParameterInternal(i));
    }

    interface Parameter extends Place<SExpression> {
    }

    class Internal {
        static class ParameterInternal implements Parameter, PlaceAdapter<SCall, SExpression> {
            private final int index;

            public ParameterInternal(int index) {
                this.index = index;
            }

            @Override
            public String _getName(SCall method) {
                return method.findDeclaration().getParameters().get(this.index).getName();
            }

            @Override
            public SType _getType(SCall method) {
                SMethodDeclaration declaration = method.findDeclaration();
                if (declaration == null) {
                    return get(method).getType();
                } else {
                    // TODO I just need delegating it to the declaration fragment
                    return declaration.getParameters().get(this.index).getType();
                }
            }

            @Override
            public SExpression _get(SCall fragment) {
                return fragment.getParameters().get(this.index);
            }

            @Override
            public void _set(SCall fragment, SExpression value) {
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
}
