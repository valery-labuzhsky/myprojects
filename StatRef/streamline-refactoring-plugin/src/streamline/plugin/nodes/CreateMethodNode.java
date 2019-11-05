package streamline.plugin.nodes;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.KeyEventDispatcher;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.nodes.guts.components.EnabledRefactoringCheckBox;
import streamline.plugin.nodes.guts.components.TreeKludgeTextField;
import streamline.plugin.refactoring.CreateMethod;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class CreateMethodNode extends RefactoringNode<CreateMethod> {
    public CreateMethodNode(CreateMethod refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setComponentFactory(CreateMethodComponent::new);
    }

    @Override
    protected Presenter createPresenter() {
        return presentation -> {
            presentation.clearText();
            presentation.addText("Create method", SimpleTextAttributes.REGULAR_ATTRIBUTES);
            // TODO for now I can just add the name later
            // TODO to place it in the middle I can use as many presenters as I choose to
            // TODO let's to the simple way for now
        };
    }

    private class CreateMethodComponent extends NodeComponent {
        private final JPanel panel = new JPanel();
        private final NodeRendererComponent renderer = new NodeRendererComponent();
        private final JTextField nodeComponent = new TreeKludgeTextField();
        // TODO it doesn't make sense to do a delegate without create method, so it should be mandatory
        // TODO so it's enabled depends on parent, what does it give us?

        public CreateMethodComponent() {
            EnabledRefactoringCheckBox enabled = new EnabledRefactoringCheckBox(CreateMethodNode.this);
            enabled.setOpaque(false); // TODO reuse it
            panel.add(enabled);
            panel.addKeyListener((KeyEventDispatcher) enabled::dispatchEvent);
            panel.add(renderer.getComponent());
            nodeComponent.setText(getRefactoring().getMethod().getName());
            nodeComponent.setOpaque(false);
            nodeComponent.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(@NotNull DocumentEvent e) {
                    panel.setSize(panel.getPreferredSize());
                    getRefactoring().getMethod().setName(nodeComponent.getText());
                    getListeners().fire();
                }
            });
            panel.add(nodeComponent);
        }

        @Override
        protected JComponent getComponent() {
            return panel;
        }

        @Override
        public boolean isEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) e;
                TreePath path = getTree().getClosestPathForLocation(me.getX(), me.getY());
                Rectangle bounds = getTree().getPathBounds(path);
                return nodeComponent.getBounds().contains(me.getX() - bounds.x, me.getY() - bounds.y);
            }
            return false;
        }

        @Override
        protected void prepare(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            renderer.prepare(tree, value, selected, expanded, leaf, row, hasFocus);
            // TODO we must somehow split the value to 2
            // TODO the value is NodeDescriptor <- CreateMethodNode
            // TODO to split it I would need break this inheritance
            panel.invalidate();
        }

    }

}
