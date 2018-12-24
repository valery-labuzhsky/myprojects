package statref.model.idea;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import statref.model.idea.expression.ILiteral;

public class IFactory {
    private static final Logger log = Logger.getInstance(IFactory.class);

    public static IElement getElement(PsiElement element) {
        if (element==null) {
            return null;
        } else if (element instanceof PsiExpression) {
            return getExpression((PsiExpression) element);
        } else if (element instanceof PsiLocalVariable) {
            return new IVariableDeclaration((PsiLocalVariable) element);
        } else if (element instanceof PsiCodeBlock) {
            return new IBlock((PsiCodeBlock) element);
        } else if (element instanceof PsiStatement) {
            return getStatement(((PsiStatement) element));
        } else if (element instanceof PsiMethod) {
            return new IMethodDeclaration((PsiMethod) element);
        }
        log.warn(element + ": is not supported");
        return null;
    }

    public static IStatement getStatement(PsiStatement statement) {
        if (statement instanceof PsiDeclarationStatement) {
            return new IDeclarationStatement((PsiDeclarationStatement) statement);
        } else if (statement instanceof PsiExpressionStatement) {
            return new IExpressionStatement((PsiExpressionStatement) statement);
        } else if (statement instanceof PsiIfStatement) {
            return new IIfStatement((PsiIfStatement) statement);
        }
        log.warn(statement+ ": is not supported");
        return null;
    }

    public static IExpression getExpression(PsiExpression expression) {
        if (expression == null) {
            return null;
        } else if (expression instanceof PsiAssignmentExpression) {
            return new IAssignment((PsiAssignmentExpression) expression);
        } else if (expression instanceof PsiLiteralExpression) {
            return new ILiteral(expression);
        }
        log.warn(expression + ": is not supported");
        return null;
    }

}
