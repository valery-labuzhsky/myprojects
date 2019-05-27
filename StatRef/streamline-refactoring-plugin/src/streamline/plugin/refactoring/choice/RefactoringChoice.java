package streamline.plugin.refactoring.choice;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.RefactoringNode;
import streamline.plugin.refactoring.Refactoring;

import java.util.ArrayList;

public class RefactoringChoice extends Refactoring {
    private Refactoring chosen;
    private final ArrayList<Refactoring> variants;

    public RefactoringChoice(ArrayList<Refactoring> variants) {
        this.variants = variants;
    }

    public Refactoring getChosen() {
        return chosen;
    }

    public void setChosen(Refactoring chosen) {
        this.chosen = chosen;
    }

    @Override
    protected void doRefactor() {
        chosen.refactor();
    }

    public static class ChoiceNode extends RefactoringNode<RefactoringChoice> {
        public ChoiceNode(Project project, RefactoringChoice refactoring, String prefix) {
            super(project, refactoring, prefix); // TODO prefix is for text as well
        }

        @NotNull
        @Override
        public SimpleNode[] createChildren() {
            // TODO we need factory
            // TODO or I can pass nodes here!
            return null;
        }

        @Override
        protected PsiElement getPsiElement() {
            return null; // TODO what for? let's make me optional!
            // TODO That's because of presentable node descriptor
            // TODO how can I solve this problem?
            // TODO 1. cope with it
            // TODO 2. make ElementNode interface!
        }
    }
}
