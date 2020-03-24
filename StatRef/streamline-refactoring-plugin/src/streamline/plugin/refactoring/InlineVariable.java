package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import statref.model.idea.IVariableDeclaration;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.VariableFlow;

public class InlineVariable extends CompoundRefactoring {
    private final IVariableDeclaration declaration;

    public InlineVariable(IVariableDeclaration declaration, RefactoringRegistry registry) {
        super(registry);
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
}
