package statref.model.idea;

import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.Nullable;
import statref.model.SMethodDeclaration;
import statref.model.SType;
import statref.model.builder.BMethod;
import statref.model.expression.SExpression;
import statref.model.expression.SMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

        @Override
        public String getName(FragmentPlace place) {
            return forFragment(place,
                    // TODO qualifier
                    m -> "t",
                    p -> {
                        // TODO without declaration
                        SMethodDeclaration declaration = findDeclaration();
                        return declaration.getParameters().get(p.index).getName();
                    });
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
        public SType getType(FragmentPlace place) {
            // TODO it really depends on the task
            return forFragment(place,
                    m -> method.getQualifier().getType(),
                    p -> {
                        SMethodDeclaration declaration = findDeclaration();
                        if (declaration == null) {
                            return method.getParameters().get(p.index).getType();
                        } else {
                            return declaration.getParameters().get(p.index).getType());
                        }
                    });
        }

        @Override
        public SExpression get() {
            return method;
        }

        private <T> T forFragment(FragmentPlace p, Function<MethodPlace, T> method, Function<ParameterPlace, T> parameter) {
            if (p instanceof MethodPlace) {
                return method.apply((MethodPlace) p);
            } else if (p instanceof ParameterPlace) {
                return parameter.apply((ParameterPlace) p);
            } else {
                throw new IllegalArgumentException("Unexpected place " + p);
            }
        }

        @Override
        public SExpression get(FragmentPlace place) {
            return forFragment(place, m -> method.getQualifier(), p -> method.getParameters().get(p.index));
        }

        @Override
        public void set(FragmentPlace place, SExpression expression) {
            forFragment(place,
                    m -> {
                        getModifiable().setQualifier(expression);
                        return null;
                    },
                    p -> {
                        getModifiable().setParameter(p.index, expression);
                        return null;
                    });
        }

        private BMethod getModifiable() {
            if (modifiable == null) {
                modifiable = new BMethod(method);
            }
            return modifiable;
        }

        @Override
        public void forEach(BiConsumer<CodeFragment, FragmentPlace> consumer) {
            List<FragmentPlace> places = new ArrayList<>();
            places.add(MethodPlace.QUALIFIER);
            for (int i = 0; i < method.getParameters().size(); i++) {
                places.add(new ParameterPlace(i));
            }
            for (FragmentPlace place : places) {
                consumer.accept(this, place);
            }
        }

        // TODO notion that any place can be identified and accessed using special index can be extended to any element
        // TODO what the difference between method name and other parameters? it's their signature!
        // TODO btw, qualifier may change signature as well if it changes a class which a method is called upon, but this is another story
        // TODO notion of fragment will disappear eventually, which is good, probably...

        // TODO but MethodDeclaration and method call will have different places, sort of, like qualifier
        // TODO yet I'd like to use them interchangeably
        // TODO how to achieve it?
        // TODO don't create MethodPlace, create ParameterPlace!
        // TODO most of places will be singletons
        // TODO I may create enums for them
        private static class ParameterPlace implements FragmentPlace {
            private final int index;

            public ParameterPlace(int index) {
                this.index = index;
            }
        }

        private enum MethodPlace implements FragmentPlace {
            QUALIFIER;
        }

    }
}
