package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.refactoring.DelegateMethod;
import streamline.plugin.refactoring.ReplaceElement;

public class DelegateNode extends CompoundNode {
    public DelegateNode(DelegateMethod refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        registry.getListeners(refactoring.getCreate()).invoke(() -> {
            for (ReplaceElement replacement : refactoring.getReplacements()) {
                registry.getListeners(replacement).fire();
            }
        });
    }
}
