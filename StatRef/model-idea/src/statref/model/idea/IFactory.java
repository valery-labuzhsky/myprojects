package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.idea.expression.ILiteral;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IFactory {
    private static final Logger log = Logger.getInstance(IFactory.class);

    private static final Registry registry = new Registry();

    private static Map<Class<?>, PsiPrimitiveType> primitives = new HashMap<>();

    static {
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

        register(PsiPrimitiveType.class, IPrimitive::new);
        // TODO I can use reflection to find all the classes and register them
        // TODO but how??
        // TODO how do we automate the process?

        primitives.put(void.class, PsiType.VOID);
    }


    private static <T> void register(Class<T> key, Function<T, Object> function) {
        registry.register(key, function);
    }

    public static class Registry {
        private final HashMap<Class, Function> registry = new HashMap<>();

        public <T> void register(Class<T> key, Function<T, Object> function) {
            registry.put(key, function);
        }

        @Nullable
        public Function findFunction(Class<?> clazz) {
            Function function = registry.get(clazz);
            if (function == null) {
                for (Class<?> intf : clazz.getInterfaces()) {
                    if (PsiElement.class.isAssignableFrom(intf)) {
                        function = findFunction(intf);
                        if (function != null) {
                            registry.put(clazz, function);
                            break;
                        }
                    }
                }
            }
            return function;
        }

        public <T> T convert(Object o) {
            Function function = findFunction(o.getClass());
            if (function!=null) {
                return (T) function.apply(o);
            }
            return null;
        }
    }

    public static <T extends IElement> T getElement(PsiElement element) {
        if (element == null) {
            return null;
        }
        T e = registry.convert(element);
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
        return registry.convert(type);
    }

    public static IType getType(Class c) {
        return getType(primitives.get(c));
    }

}
