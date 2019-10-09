package statref.model.expressions;

import statref.model.members.SMethodDeclaration;
import statref.model.types.SType;
import statref.model.fragment.Place;
import statref.model.fragment.PlaceAdapter;

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
            places.add(new Parameter(i));
        }
        return places;
    }

    class Parameter implements PlaceAdapter<SMethod, SExpression> {
        // TODO notion that any place can be identified and accessed using special index can be extended to any element
        // TODO what the difference between method name and other parameters? it's their signature!
        // TODO name doesn't matter for a while
        // TODO btw, qualifier may change signature as well if it changes a class which a method is called upon, but this is another story
        // TODO notion of fragment will disappear eventually, which is good, probably...

        // TODO but MethodDeclaration and method call will have different places, sort of, like qualifier
        // TODO yet I'd like to use them interchangeably
        // TODO how to achieve it?
        // TODO don't create MethodPlace, create ParameterPlace!
        // TODO most of places will be singletons
        // TODO I may create enums for them
        private final int index;

        public Parameter(int index) {
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
            Parameter that = (Parameter) o;
            return index == that.index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index);
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
