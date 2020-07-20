package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.ILocalVariableDeclaration;
import statref.model.idea.expressions.ILocalVariable;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.VariableFlow;

import java.util.Objects;

public class InlineVariable extends SimpleCompoundRefactoring {
    private final ILocalVariableDeclaration declaration;

    public InlineVariable(ILocalVariableDeclaration declaration, RefactoringRegistry registry) {
        super(registry, declaration);
        VariableFlow flow = new VariableFlow(declaration);
        this.declaration = flow.getDeclaration();
        add(registry.getRefactoring(new InlineAssignment(registry, this.declaration)));
        for (ILocalVariable assignment : flow.getAssignments()) {
            add(registry.getRefactoring(new InlineAssignment(registry, (IInitializer) assignment.getParent())));
        }
        // TODO First step is to show full tree
        // TODO Second step - simplify tree
        // TODO Now I need create this node
    }

    public ILocalVariableDeclaration getDeclaration() {
        return declaration;
    }

    public InlineUsage enableOnly(ILocalVariable usage) {
        disableAll();

        return getRefactorings().map(r -> (InlineAssignment) r).
                map(r -> r.enableOnly(usage)).
                filter(Objects::nonNull).findFirst().
                map(r -> {
                    r.setEnabled(true);
                    return r;
                }).orElse(null);
    }

    private void disableAll() {
        // TODO it will cause massive listeners response
        //  anything I can do about it?
        //  disable me first
//        setEnabled(false);
        getRefactorings().forEach(r -> r.setEnabled(false));
    }

    public InlineAssignment enableOnly(IInitializer variable) {
        disableAll();

        return getRefactorings().map(r -> (InlineAssignment) r).
                filter(r -> r.getInitializer().equals(variable)).findFirst().
                map(r -> {
                    r.setEnabled(true);
                    return r;
                }).orElse(null);
    }
}
