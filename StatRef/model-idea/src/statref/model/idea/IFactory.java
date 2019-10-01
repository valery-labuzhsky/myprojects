package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import statref.model.*;
import statref.model.expression.SExpression;
import statref.model.idea.expression.ILiteral;

import java.util.HashMap;
import java.util.Map;

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
            register(PsiMethodCallExpression.class, IMethod::new);
            register(PsiClass.class, IClassDeclaration::new);
            register(PsiReturnStatement.class, IReturn::new);
            // TODO generate it!
        }
    };

    private static final BiFunctionRegistry<SElement, Project, IElement> model2idea = new BiFunctionRegistry<SElement, Project, IElement>() {
        {
            register(SMethodDeclaration.class, IFactory::convertMethodDeclaration);
            register(SStatement.class, IFactory::convertStatement);
            register(SExpression.class, IFactory::convertExpression);
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
        IElement idea = model2idea.convert(element, project);
        if (idea == null) {
            return getUnknownElement(JavaPsiFacade.getElementFactory(project).createExpressionFromText(element.getText(), null));
        }
        return idea;
    }

    public static IExpression convertExpression(SExpression expression, Project project) {
        return getElement(JavaPsiFacade.getElementFactory(project).createExpressionFromText(expression.getText(), null));
    }

    @NotNull
    public static IMethodDeclaration convertMethodDeclaration(SMethodDeclaration prototype, Project project) {
        // TODO generate it from text?
        SType type = prototype.getReturnType();
        PsiType psiType = toPsiType(project, type);
        PsiMethod newMethod = JavaPsiFacade.getElementFactory(project).createMethod(prototype.getName(), psiType);

        for (SStatement instruction : prototype.getInstructions()) {
            newMethod.getBody().add(convert(project, instruction).getElement());
        }
        return getElement(newMethod);
    }

    @Nullable
    public static PsiType toPsiType(Project project, SType type) {
        PsiType psiType = null;
        if (type instanceof IType) {
            psiType = ((IType) type).getPsiType();
        } else if (type instanceof SClass) {
            psiType = PsiType.getTypeByName(((SClass) type).getName(), project, GlobalSearchScope.allScope(project));
        }
        if (psiType == null) {
            log.error("Failed to find PsiType for " + type + " of " + type.getClass().getName());
        }
        return psiType;
    }

    @NotNull
    public static IStatement convertStatement(SStatement prototype, Project project) {
        return getElement(JavaPsiFacade.getElementFactory(project).createStatementFromText(prototype.getText(), null));
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

}
