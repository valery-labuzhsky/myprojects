package streamline.plugin.refactoring.usage;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;
import streamline.plugin.nodes.SelfPresentingNode;

public class VariantsNode extends SelfPresentingNode {
    private final VariantElementNode[] nodes;
    @NotNull
    private final InlineUsage refactoring;

    public VariantsNode(Project project, @NotNull InlineUsage refactoring) {
        super(project);
        this.refactoring = refactoring;
        nodes = this.refactoring.getVariants().stream().map(variant -> new VariantElementNode(myProject, this, variant))
                .toArray(VariantElementNode[]::new);
        update();
    }



    @Override
    protected void doUpdate() {
        PresentationData presentation = getTemplatePresentation();
        presentation.clearText();

        presentation.addText("Conflict: there are many possible values", SimpleTextAttributes.ERROR_ATTRIBUTES);
    }

    @NotNull
    @Override
    public SimpleNode[] getChildren() {
        return nodes;
    }

    public IElement getSelected() {
        return refactoring.getSelected();
    }

    public void setSelected(IElement variant) {
        refactoring.setSelected(variant);
        for (VariantElementNode node : nodes) {
            node.fireVariantChanged();
        }
    }

}
