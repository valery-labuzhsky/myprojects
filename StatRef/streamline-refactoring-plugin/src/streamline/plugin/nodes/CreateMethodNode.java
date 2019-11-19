package streamline.plugin.nodes;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.nodes.guts.components.EnabledRefactoringCheckBox;
import streamline.plugin.nodes.guts.components.TreeKludgeTextField;
import streamline.plugin.refactoring.CreateMethod;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class CreateMethodNode extends RefactoringNode<CreateMethod> {
    public CreateMethodNode(CreateMethod refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setComponentFactory(() -> createNodeComponent(this));
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

    public static NodePanel createNodeComponent(CreateMethodNode node) {
        final NodePanel nodeComponent = new NodePanel();
        EnabledRefactoringCheckBox enabled = new EnabledRefactoringCheckBox(node);
        nodeComponent.add(enabled);
        nodeComponent.dispatchKeyEvents(enabled);

        nodeComponent.addNodeComponent(new TextRenderer(node.getProject(), new SimplePresenter().add("Create method")));

        JTextField editor = new TreeKludgeTextField();
        editor.setText(node.getRefactoring().getMethod().getName());
        editor.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                nodeComponent.resize();
                node.getRefactoring().getMethod().setName(editor.getText());
                node.getListeners().fire();
            }
        });
        nodeComponent.addEditor(editor);
        return nodeComponent;
    }

}
