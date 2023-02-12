package streamline.plugin.nodes;

import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import streamline.plugin.nodes.guts.*;
import streamline.plugin.nodes.guts.components.TreeKludgeTextField;
import streamline.plugin.refactoring.CreateMethod;
import streamline.plugin.refactoring.guts.Refactoring;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.function.Consumer;

public class CreateMethodNode extends RefactoringNode {
    public CreateMethodNode(CreateMethod refactoring, NodesRegistry registry) {
        super(refactoring, registry);
        setNodePanelParts(enabledCheckBox(), textRenderer(createPresenter()), nameEditor());
    }

    @Override
    public CreateMethod getRefactoring() {
        return (CreateMethod) super.getRefactoring();
    }

    protected Presenter createPresenter() {
        return new SimplePresenter().add("Create method");
    }

    @NotNull
    private Consumer<NodePanel> nameEditor() {
        return panel -> {
            JTextField editor = new TreeKludgeTextField();
            editor.setText(this.getRefactoring().getMethod().getName());
            editor.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(@NotNull DocumentEvent e) {
                    panel.resize();
                    getRefactoring().getMethod().setName(editor.getText());
                    getListeners().fire();
                }
            });
            panel.addEditor(editor);
        };
    }

}
