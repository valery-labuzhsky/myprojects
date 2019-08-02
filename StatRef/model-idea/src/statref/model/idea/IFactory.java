package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.SElement;
import statref.model.SStatement;
import statref.model.SMethodDeclaration;
import statref.model.idea.expression.ILiteral;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IFactory {
    private static final Logger log = Logger.getInstance(IFactory.class);

    private static final FunctionRegistry<PsiElement, IElement> psi2model = new FunctionRegistry<PsiElement, IElement>() {
        {
            register(PsiLocalVariable.class, IVariableDeclaration::new);
            register(PsiCodeBlock.class, IBlock::new);
            register(PsiMethod.class, IMethodDeclaration::new);
            register(PsiParameter.class, IParameter::new);
            register(PsiClass.class, IClassDeclaration::new);
            register(PsiDeclarationStatement.class, IDeclarationStatement::new);
            register(PsiExpressionStatement.class, IExpressionStatement::new);
            register(PsiIfStatement.class, IIfStatement::new);
            register(PsiBlockStatement.class, IBlockStatement::new);
            register(PsiWhileStatement.class, IWhileStatement::new);
            register(PsiDoWhileStatement.class, IDoWhileStatement::new);
            register(PsiForStatement.class, IForStatement::new);
            register(PsiForeachStatement.class, IForEachStatement::new);
            register(PsiReferenceExpression.class, IVariable::new);
            register(PsiAssignmentExpression.class, IAssignment::new);
            register(PsiLiteralExpression.class, ILiteral::new);
            register(PsiBinaryExpression.class, IBinaryExpression::new);
            register(PsiMethodCallExpression.class, IMethodCall::new);
            register(PsiClass.class, IClassDeclaration::new);
            register(PsiReturnStatement.class, IReturn::new);
            // TODO generate it!
        }
    };

    private static final BiFunctionRegistry<SElement, Project, IElement> model2idea = new BiFunctionRegistry<SElement, Project, IElement>() {
        {
            register(SMethodDeclaration.class, IFactory::convertMethodDeclaration);
            register(SStatement.class, IFactory::convertStatement);
            // TODO generate it!
        }
    };

    private static final FunctionRegistry<PsiType, IType> psitypes = new FunctionRegistry<PsiType, IType>() {
        {
            register(PsiPrimitiveType.class, IPrimitive::new);
            // TODO generate it!
        }
    };

    private static Map<Class<?>, PsiPrimitiveType> primitives = new HashMap<>();

    static {
        primitives.put(void.class, PsiType.VOID);
    }

    public static IElement convert(Project project, SElement element) {
        if (element instanceof IElement) {
            return (IElement) element;
        }
        IElement idea = model2idea.convert(project, element);
        if (idea == null) {
            return getUnknownElement(JavaPsiFacade.getElementFactory(project).createExpressionFromText(element.getText(), null));
        }
        return idea;
    }

    @NotNull
    public static IMethodDeclaration convertMethodDeclaration(Project project, SMethodDeclaration prototype) {
        // TODO generate it from text?
        IType type = (IType) prototype.getReturnType(); // TODO do the proper conversion: it may not be IType
        PsiMethod newMethod = JavaPsiFacade.getElementFactory(project).createMethod(prototype.getName(), type.getPsiType());

        for (SStatement instruction : prototype.getInstructions()) {
            newMethod.getBody().add(convert(project, instruction).getElement());
        }
        return getElement(newMethod);
    }

    @NotNull
    public static IStatement convertStatement(Project project, SStatement prototype) {
        return getElement(JavaPsiFacade.getElementFactory(project).createStatementFromText(prototype.getText(), null));
    }

    public static class ClassRegistry<K, V> {
        private final HashMap<Class<K>, V> registry = new LinkedHashMap<>();

        public void register(Class<K> key, V value) {
            registry.put(key, value);
        }

        @Nullable
        public V get(Class<K> clazz) {
            V value = registry.get(clazz);
            if (value == null) {
                for (Map.Entry<Class<K>, V> entry : registry.entrySet()) {
                    if (entry.getKey().isAssignableFrom(clazz)) {
                        value = entry.getValue();
                        registry.put(clazz, value);
                        break;
                    }
                }
            }
            return value;
        }

    }

    public static <T extends IElement> T getElement(PsiElement element) {
        if (element == null) {
            return null;
        }
        T e = (T) psi2model.convert(element);
        if (e == null) {
            return (T) getUnknownElement(element);
        }
        return e;
    }

    @NotNull
    private static IElement getUnknownElement(PsiElement element) {
        log.error(element + ": is not supported");
        return new IUnknownElement(element);
    }

    public static IType getType(PsiType type) {
        return (IType) psitypes.convert(type);
    }

    public static IType getType(Class c) {
        return getType(primitives.get(c));
    }

    private static class FunctionRegistry<K, V> extends ClassRegistry<K, Function<K, V>> {

        public <P extends K> void register(Class<P> key, Function<P, V> value) {
            super.register((Class) key, (Function) value);
        }

        public <T> T convert(Object o) {
            Function function = get((Class<K>) o.getClass());
            if (function != null) {
                return (T) function.apply(o);
            }
            return null;
        }
    }

    private static class BiFunctionRegistry<K, U, R> extends ClassRegistry<K, BiFunction<U, K, R>> {

        public <P extends K> void register(Class<P> key, BiFunction<U, P, R> value) {
            super.register((Class) key, (BiFunction) value);
        }

        public <T> T convert(U u, Object o) {
            BiFunction function = get((Class<K>) o.getClass());
            if (function != null) {
                return (T) function.apply(u, o);
            }
            return null;
        }
    }

}
