package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.VariableFlow;

public class InlineVariable extends CompoundRefactoring {
    private final IVariableDeclaration declaration;

    public InlineVariable(IVariableDeclaration declaration, RefactoringRegistry registry) {
        super(registry, declaration);
        VariableFlow flow = new VariableFlow(declaration);
        this.declaration = flow.getDeclaration();
        add(registry.getRefactoring(new InlineAssignment(registry, this.declaration)));
        for (IVariable assignment : flow.getAssignments()) {
            add(registry.getRefactoring(new InlineAssignment(registry, (IInitializer) assignment.getParent())));
        }
        // TODO First step is to show full tree
        // TODO Second step - simplify tree
        // TODO Now I need create this node
    }

    public IVariableDeclaration getDeclaration() {
        return declaration;
    }

    public InlineUsage enableOnly(IVariable usage) {
        InlineUsage enabled = null;
        for (Refactoring refactoring : getRefactorings()) {
            if (refactoring instanceof InlineAssignment) {
                InlineAssignment assignment = (InlineAssignment) refactoring;
                if (enabled == null) {
                    enabled = assignment.enableOnly(usage);
                    assignment.setEnabled(enabled != null);
                } else {
                    assignment.setEnabled(false);
                }
            }
        }
        return enabled;
    }

    public InlineAssignment enableOnly(IInitializer variable) {
        InlineAssignment enabled = null;
        for (Refactoring refactoring : getRefactorings()) {
            if (refactoring instanceof InlineAssignment) {
                InlineAssignment assignment = (InlineAssignment) refactoring;
                if (enabled == null && assignment.getInitializer().equals(variable)) {
                    assignment.setEnabled(true);
                    enabled = assignment;
                } else {
                    assignment.setEnabled(false);
                }
            }
        }
        return enabled;
    }
}
