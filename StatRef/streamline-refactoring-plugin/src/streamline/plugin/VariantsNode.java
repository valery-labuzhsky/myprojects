package streamline.plugin;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.annotations.NotNull;

public class VariantsNode extends SimpleNode {
    private final SimpleNode[] nodes;
    @NotNull
    private final AssignmentVariants variants;

    public VariantsNode(Project project, AssignmentVariants variants) {
        super(project);
        nodes = variants.getVariants().stream().map(variant -> new VariantElementNode(myProject, variant)).toArray(SimpleNode[]::new);
        this.variants = variants;
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
}
