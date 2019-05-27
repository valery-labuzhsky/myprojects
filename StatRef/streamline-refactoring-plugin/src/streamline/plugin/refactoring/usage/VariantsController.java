package streamline.plugin.refactoring.usage;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IElement;

import java.util.List;
import java.util.stream.Collectors;

public class VariantsController {
    private final List<VariantElementNode> nodes;
    @NotNull
    private final InlineUsage refactoring;

    public VariantsController(InlineUsage refactoring) {
        this.refactoring = refactoring;
        nodes = refactoring.getVariants().stream().map(
                variant -> new VariantElementNode(this, variant))
                .collect(Collectors.toList());
    }

    public List<VariantElementNode> getNodes() {
        return nodes;
    }

    public IElement getSelected() {
        return refactoring.getSelected();
    }

    public void setSelected(IElement variant) {
        refactoring.setSelected(variant);
        // TODO I don't need to know parent, I need something which will bind all variants
        for (VariantElementNode node : nodes) {
            node.fireVariantChanged();
        }
    }
}
