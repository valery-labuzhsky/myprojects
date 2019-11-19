package streamline.plugin.nodes;

import streamline.plugin.nodes.guts.NodesRegistry;
import streamline.plugin.refactoring.Delegate;
import streamline.plugin.refactoring.ReplaceElement;

public class DelegateNode extends CompoundNode<Delegate> {
    public DelegateNode(Delegate refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        registry.getListeners(refactoring.getCreate()).invoke(() -> {
            for (ReplaceElement replacement : refactoring.getReplacements()) {
                registry.getListeners(replacement).fire();
            }
        });
    }
}
