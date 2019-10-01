package statref.model.idea;

import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.SMethodDeclaration;
import statref.model.SType;
import statref.model.builder.BMethod;
import statref.model.expression.SExpression;
import statref.model.expression.SMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IMethod extends IExpression<PsiMethodCallExpression> implements SMethod {
    public IMethod(PsiMethodCallExpression expression) {
        super(expression);
    }

    public IExpression getExpression(IParameter parameter) {
        return getElement(getElement().getArgumentList().getExpressions()[parameter.getIndex()]);
    }

    @Override
    public SExpression getQualifier() {
        return getElement(getElement().getMethodExpression().getQualifierExpression());
    }

    @Override
    public String getName() {
        return getElement().getMethodExpression().getReferenceName();
    }

    @Override
    public List<IExpression> getParameters() {
        return new IElementList<>(getElement().getArgumentList().getExpressions());
    }

    @Override
    public Fragment fragment() {
        return new MethodFragment(this);
    }

    @Override
    public SMethodDeclaration findDeclaration() {
        return IFactory.getElement(getElement().resolveMethod());
    }

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
    public static class ParameterPlace implements PlaceAdapter<MethodFragment, SExpression> {
        private final int index;

        public ParameterPlace(int index) {
            this.index = index;
        }

        @Override
        public String _getName(MethodFragment fragment) {
            SMethodDeclaration declaration = fragment.findDeclaration();
            return declaration.getParameters().get(this.index).getName();
        }

        @Override
        public SType _getType(MethodFragment fragment) {
            SMethodDeclaration declaration = fragment.findDeclaration();
            if (declaration == null) {
                return fragment.method.getParameters().get(this.index).getType();
            } else {
                // TODO I just need delegating it to the declaration fragment
                return declaration.getParameters().get(this.index).getType();
            }
        }

        @Override
        public SExpression _get(MethodFragment fragment) {
            return fragment.method.getParameters().get(this.index);
        }

        @Override
        public void _set(MethodFragment fragment, SExpression value) {
            fragment.getModifiable().setParameter(this.index, value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParameterPlace that = (ParameterPlace) o;
            return index == that.index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index);
        }
    }

    public static class MethodFragment extends Fragment {
        private final SMethod method;
        private BMethod modifiable;

        public MethodFragment(SMethod method) {
            this.method = method;
        }

        @Nullable
        private SMethodDeclaration findDeclaration() {
            return method.findDeclaration();
        }

        @Override
        public SExpression get() {
            if (modifiable != null) {
                return modifiable;
            }
            return method;
        }

        private BMethod getModifiable() {
            // TODO I need yet another interface Method, or should I just add these methods to the interface
            // TODO it will be better, it very depends on implementation
            if (modifiable == null) {
                modifiable = new BMethod(method);
            }
            return modifiable;
        }

        @Override
        @NotNull
        public List<Place<SExpression>> getExpressions() {
            List<Place<SExpression>> places = new ArrayList<>();
            places.add(QualifierPlace.QUALIFIER);
            for (int i = 0; i < method.getParameters().size(); i++) {
                places.add(new ParameterPlace(i));
            }
            return places;
        }

        private static class QualifierPlace implements PlaceAdapter<MethodFragment, SExpression> {
            public static final Place<SExpression> QUALIFIER = new QualifierPlace();

            @Override
            public String _getName(MethodFragment fragment) {
                return "t";
            }

            @Override
            public SType _getType(MethodFragment fragment) {
                SExpression qualifier = _get(fragment);
                if (qualifier == null) {
                    // TODO check if it's this
                    return null;
                }
                return qualifier.getType(); // TODO use declaration
            }

            @Override
            public SExpression _get(MethodFragment fragment) {
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
                return fragment.method.getQualifier();
            }

            @Override
            public void _set(MethodFragment fragment, SExpression value) {
                fragment.getModifiable().setQualifier(value);
            }

            @Override
            public int hashCode() {
                return QualifierPlace.class.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof QualifierPlace;
            }
        }

    }
}
