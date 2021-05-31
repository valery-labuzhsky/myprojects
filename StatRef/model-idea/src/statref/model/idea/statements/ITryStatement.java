package statref.model.idea.statements;

import com.intellij.psi.PsiTryStatement;

/**
 * Created on 03.12.2020.
 *
 * @author unicorn
 */
public class ITryStatement extends IStatement {
    public ITryStatement(PsiTryStatement element) {
        super(element);
    }
}
