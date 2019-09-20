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
    public CodeFragment fragment() {
        return new MethodFragment(this);
    }

    public static class MethodFragment extends CodeFragment {
        private final SMethod method;
        private BMethod modifiable;

        public MethodFragment(SMethod method) {
            this.method = method;
        }

        @Nullable
        private SMethodDeclaration findDeclaration() {
            SMethodDeclaration declaration = null;
            if (method instanceof IMethod) {
                declaration = IFactory.getElement(((IMethod) method).getElement().resolveMethod());
            }
            return declaration;
        }

        @Override
        public SExpression get() {
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
        public List<FragmentPlace<SExpression>> getExpressions() {
            List<FragmentPlace<SExpression>> places = new ArrayList<>();
            places.add(QualifierPlace.QUALIFIER);
            for (int i = 0; i < method.getParameters().size(); i++) {
                places.add(new MethodParameterPlace(i));
            }
            return places;
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
        private static class MethodParameterPlace extends FragmentPlace<SExpression> {
            public MethodParameterPlace(int index) {
                super(new InternalMethodParameterPlace(index));
            }
        }

        private static class InternalMethodParameterPlace implements InternalFragmentPlace<MethodFragment, SExpression> {
            private final int index;

            public InternalMethodParameterPlace(int index) {
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
        }

        private static class QualifierPlace implements InternalFragmentPlace<MethodFragment, SExpression> {
            public static final FragmentPlace<SExpression> QUALIFIER = new FragmentPlace<>(new QualifierPlace());

            @Override
            public String _getName(MethodFragment fragment) {
                return "t";
            }

            @Override
            public SType _getType(MethodFragment fragment) {
                return fragment.method.getQualifier().getType(); // TODO use declaration
            }

            @Override
            public SExpression _get(MethodFragment fragment) {
                return fragment.method.getQualifier();
            }

            @Override
            public void _set(MethodFragment fragment, SExpression value) {
                fragment.getModifiable().setQualifier(value);
            }
        }

    }
}
