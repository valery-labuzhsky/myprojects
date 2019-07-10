package statref.model.idea;

import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;
import statref.model.idea.expression.ILiteral;

import java.util.HashMap;
import java.util.function.Function;

public class IFactory {
    private static final HashMap<Class<PsiElement>, Function<PsiElement, IElement>> registry = new HashMap<>();

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
        // TODO I can use reflection to find all the classes and register them
        // TODO but how??
        // TODO how do we automate the process?
    }

    public static <T extends PsiElement> void register(Class<T> key, Function<T, IElement> function) {
        registry.put((Class<PsiElement>) key, (Function<PsiElement, IElement>) function);
    }

    public static <T extends IElement> T getElement(PsiElement element) {
        if (element == null) {
            return null;
        }
        Class<? extends PsiElement> clazz = element.getClass();
        Function<PsiElement, IElement> constructor = findConstructor(clazz);
        if (constructor == null) {
            throw new IllegalArgumentException(element + ": is not supported");
        }
        return (T) constructor.apply(element);
    }

    @Nullable
    public static Function<PsiElement, IElement> findConstructor(Class<?> clazz) {
        Function<PsiElement, IElement> constructor = registry.get(clazz);
        if (constructor == null) {
            for (Class<?> intf : clazz.getInterfaces()) {
                if (PsiElement.class.isAssignableFrom(intf)) {
                    constructor = findConstructor(intf);
                    if (constructor != null) {
                        registry.put((Class<PsiElement>) clazz, constructor);
                        break;
                    }
                }
            }
        }
        return constructor;
    }

    public static IType getType(PsiType type) {
        return new IUnknownType(type);
    }

}
