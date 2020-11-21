package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import statref.model.SElement;
import statref.model.expressions.SExpression;
import statref.model.idea.expressions.*;
import statref.model.idea.statements.*;
import statref.model.members.SMethodDeclaration;
import statref.model.statements.SStatement;
import statref.model.types.SType;

public class IFactory {
    private static final Logger log = Logger.getInstance(IFactory.class);

    private static final FunctionRegistry<PsiElement, IElement> psi2model = new FunctionRegistry<PsiElement, IElement>() {
        {
            register(PsiClass.class, IClassDeclaration::new);
            register(PsiMethod.class, IMethodDeclaration::new);
            register(PsiParameter.class, IParameter::new);

            register(PsiCodeBlock.class, IBlock::new);
            register(PsiLocalVariable.class, ILocalVariableDeclaration::new);

            register(PsiDeclarationStatement.class, IDeclarationStatement::new);
            register(PsiExpressionStatement.class, IExpressionStatement::new);
            register(PsiIfStatement.class, IIfStatement::new);
            register(PsiBlockStatement.class, IBlockStatement::new);
            register(PsiWhileStatement.class, IWhileStatement::new);
            register(PsiDoWhileStatement.class, IDoWhileStatement::new);
            register(PsiForStatement.class, IForStatement::new);
            register(PsiForeachStatement.class, IForEachStatement::new);
            register(PsiReturnStatement.class, IReturn::new);

            register(PsiReferenceExpression.class, IReference::create);
            register(PsiAssignmentExpression.class, IAssignment::new);
            register(PsiLiteralExpression.class, ILiteral::new);
            register(PsiBinaryExpression.class, IBinaryExpression::new);
            register(PsiMethodCallExpression.class, IMethod::new);
            register(PsiConditionalExpression.class, IConditional::new);
            register(PsiLambdaExpression.class, ILambdaExpression::new);
            register(PsiNewExpression.class, INewExpression::new);
            register(PsiTypeCastExpression.class, ITypeCastExpression::new);
            // TODO generate it!
            //  how can I do it?
            //   1. annotation processor: is not ideal as it requires annotations and what to do with partial compilation?
            //   2. I need to search through all the classes, all I need is a list
            //   3. BEST! I can use my plugin to generate a code for itself
            //   4. https://stackoverflow.com/questions/435890/find-java-classes-implementing-an-interface/435930

            register(PsiJavaCodeReferenceElement.class, IClassReference::new); // it's parent class for references
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

    private static IExpression convertExpression(SExpression expression, Project project) {
        return getElement(JavaPsiFacade.getElementFactory(project).createExpressionFromText(expression.getText(), null));
    }

    @NotNull
    public static IMethodDeclaration convertMethodDeclaration(SMethodDeclaration prototype, Project project) {
        // TODO generate it from text?
        SType type = prototype.getReturnType();
        PsiType psiType = ITypes.toPsiType(project, type);
        PsiMethod newMethod = JavaPsiFacade.getElementFactory(project).createMethod(prototype.getName(), psiType);

        for (SStatement instruction : prototype.getInstructions()) {
            newMethod.getBody().add(convert(project, instruction).getElement());
        }
        return getElement(newMethod);
    }

    @NotNull
    private static IStatement convertStatement(SStatement prototype, Project project) {
        return getElement(JavaPsiFacade.getElementFactory(project).createStatementFromText(prototype.getText(), null));
    }

    public static <T extends IElement> T getElement(PsiElement element) {
        if (element == null) {
            return null;
        }
        T e = (T) psi2model.convert(element);
        if (e == null) {
            if (element instanceof PsiExpression) {
                return (T) getUnknownExpression((PsiExpression) element);
            }
            return (T) getUnknownElement(element);
        }
        return e;
    }

    @NotNull
    private static IExpression getUnknownExpression(PsiExpression element) {
        log.error(element + ": is not supported");
        return new IUnknownExpression(element);
    }

    @NotNull
    private static IElement getUnknownElement(PsiElement element) {
        log.error(element + ": is not supported");
        return new IUnknownElement(element);
    }

}
