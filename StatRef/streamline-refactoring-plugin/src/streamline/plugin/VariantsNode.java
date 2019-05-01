package streamline.plugin;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;

public class VariantsNode extends SelfPresentingNode {
    private final VariantElementNode[] nodes;
    @NotNull
    private final AssignmentVariants variants;

    public VariantsNode(Project project, AssignmentVariants variants) {
        super(project);
        nodes = variants.getVariants().stream().map(variant -> new VariantElementNode(myProject, this, variant))
                .toArray(VariantElementNode[]::new);
        this.variants = variants;
        update();
    }

    @Override
    protected void doUpdate() {
        PresentationData presentation = getTemplatePresentation();
        presentation.clearText();

        // TODO I must change text depending on variants available
        presentation.addText("Conflict: there are many possible values", SimpleTextAttributes.ERROR_ATTRIBUTES);
    }

    @NotNull
    @Override
    public SimpleNode[] getChildren() {
        return nodes;
    }

    public IElement getSelected() {
        return variants.getSelected();
    }

    public void setSelected(IElement variant) {
        variants.setSelected(variant);
        for (VariantElementNode node : nodes) {
            node.fireVariantChanged();
        }
    }
}
