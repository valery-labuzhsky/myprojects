package statref.model.idea;

import com.intellij.psi.PsiIfStatement;
import statref.model.SContext;
import statref.model.SElement;
import statref.model.STraceContext;

import java.util.Objects;

public class IIfStatement extends IStatement<PsiIfStatement> {
    public IIfStatement(PsiIfStatement statement) {
        super(statement);
    }

    @Override
    public SContext getContext(SElement element) {
        if (getThenBranch().equals(element)) {
            return new ThenContext(this);
        } else {
            IStatement branch = getElseBranch();
            if (branch!=null) {
                return new ElseContext(this);
            }
        }
        return null;
    }

    public IStatement getElseBranch() {
        return IFactory.getStatement(getElement().getElseBranch());
    }

    public IStatement getThenBranch() {
        return IFactory.getStatement(getElement().getThenBranch());
    }

    public abstract static class IfContext implements STraceContext {
        private final IIfStatement statement;

        public IfContext(IIfStatement statement) {
            this.statement = statement;
        }

        @Override
        public SContext getContext() {
            return statement.getContext();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IfContext ifContext = (IfContext) o;
            return Objects.equals(statement, ifContext.statement);
        }

        @Override
        public int hashCode() {
            return Objects.hash(statement);
        }
    }

    public static class ThenContext extends IfContext {
        public ThenContext(IIfStatement statement) {
            super(statement);
        }
    }

    public static class ElseContext extends IfContext {
        public ElseContext(IIfStatement statement) {
            super(statement);
        }
    }
}
