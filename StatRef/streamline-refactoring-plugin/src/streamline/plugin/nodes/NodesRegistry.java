package streamline.plugin.nodes;

import com.intellij.openapi.project.Project;
import statref.model.idea.BiFunctionRegistry;
import streamline.plugin.CreateMethod;
import streamline.plugin.InlineParameter;
import streamline.plugin.ReplaceElement;
import streamline.plugin.refactoring.Listeners;
import streamline.plugin.refactoring.Refactoring;
import streamline.plugin.refactoring.RefactoringRegistry;
import streamline.plugin.refactoring.assignment.InlineAssignment;
import streamline.plugin.refactoring.assignment.InlineAssignmentNode;
import streamline.plugin.refactoring.compound.CompoundNode;
import streamline.plugin.refactoring.compound.CompoundRefactoring;
import streamline.plugin.refactoring.usage.InlineUsage;
import streamline.plugin.refactoring.usage.InlineUsageNode;

import java.util.HashMap;

public class NodesRegistry {
    private static final BiFunctionRegistry<Refactoring, NodesRegistry, RefactoringNode> NODES = new BiFunctionRegistry<Refactoring, NodesRegistry, RefactoringNode>() {{
        register(InlineUsage.class, InlineUsageNode::new);
        register(InlineAssignment.class, InlineAssignmentNode::new);
        register(InlineParameter.class, InlineParameterNode::new);
        register(CompoundRefactoring.class, CompoundNode::new);
        register(CreateMethod.class, CreateMethodNode::new);
        register(ReplaceElement.class, ReplaceElementNode::new);
    }};

    private final RefactoringRegistry refactorings = new RefactoringRegistry();
    private final HashMap<Refactoring, Listeners> listeners = new HashMap<>();

    private final Project project;

    public NodesRegistry(Project project) {
        this.project = project;
    }

    public <N extends RefactoringNode> N create(Refactoring refactoring) {
        N node = NODES.convert(refactoring, this);
        if (node == null) {
            throw new IllegalArgumentException("Unknown refactoring " + refactoring.getClass().getSimpleName());
        }
        return node;
    }

    public Project getProject() {
        return project;
    }

    public RefactoringRegistry getRefactorings() {
        return refactorings;
    }

    public Listeners getListeners(Refactoring refactoring) {
        return listeners.computeIfAbsent(refactoring, (r) -> new Listeners());
    }

}
