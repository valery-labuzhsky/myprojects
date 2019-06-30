package statref.model.idea;

import com.intellij.psi.*;
import statref.model.idea.expression.ILiteral;

public class IFactory {

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
        throw new IllegalArgumentException(element + ": is not supported");
    }

    public static IStatement getStatement(PsiStatement statement) {
        if (statement instanceof PsiDeclarationStatement) {
            return new IDeclarationStatement((PsiDeclarationStatement) statement);
        } else if (statement instanceof PsiExpressionStatement) {
            return new IExpressionStatement((PsiExpressionStatement) statement);
        } else if (statement instanceof PsiIfStatement) {
            return new IIfStatement((PsiIfStatement) statement);
        } else if (statement instanceof PsiBlockStatement) {
            return new IBlockStatement((PsiBlockStatement) statement);
        } else if (statement instanceof PsiLoopStatement) {
            return getLoopStatement((PsiLoopStatement) statement);
        }
        throw new IllegalArgumentException(statement + ": is not supported");
    }

    private static IStatement getLoopStatement(PsiLoopStatement statement) {
        if (statement instanceof PsiWhileStatement) {
            return new IWhileStatement((PsiWhileStatement) statement);
        } else if (statement instanceof PsiDoWhileStatement) {
            return new IDoWhileStatement((PsiDoWhileStatement) statement);
        } else if (statement instanceof PsiForStatement) {
            return new IForStatement((PsiForStatement) statement);
        } else if (statement instanceof PsiForeachStatement) {
            return new IForEachStatement((PsiForeachStatement) statement);
        }
        throw new IllegalArgumentException(statement + ": is not supported");
    }

    public static IExpression getExpression(PsiExpression expression) {
        if (expression == null) {
            return null;
        } else if (expression instanceof PsiReferenceExpression) {
            return new IVariable((PsiReferenceExpression) expression);
        } else if (expression instanceof PsiAssignmentExpression) {
            return new IAssignment((PsiAssignmentExpression) expression);
        } else if (expression instanceof PsiLiteralExpression) {
            return new ILiteral(expression);
        } else if (expression instanceof PsiBinaryExpression) {
            return new IBinaryExpression((PsiBinaryExpression) expression);
        }
        throw new IllegalArgumentException(expression + ": is not supported");
    }

}
