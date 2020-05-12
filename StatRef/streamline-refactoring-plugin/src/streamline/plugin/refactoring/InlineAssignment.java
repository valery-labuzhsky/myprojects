package streamline.plugin.refactoring;

import org.jetbrains.annotations.NotNull;
import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.VariableFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class InlineAssignment extends CompoundRefactoring {
    private final IInitializer initializer;

    private final ArrayList<InlineUsage> usages = new ArrayList<>();
    private final RemoveElement remove;

    public InlineAssignment(RefactoringRegistry registry, IInitializer initializer) {
        super(registry, initializer);
        this.initializer = initializer;
        remove = new RemoveElement(registry, initializer);
        VariableFlow flow = new VariableFlow(initializer.declaration());
        Collection<IVariable> usages = flow.getUsages(initializer);
        for (IVariable usage : usages) {
            addUsage(usage);
        }
        remove.setEnabled(!areUsagesLeft());
    }

    private void addUsage(IVariable usage) {
        InlineUsage inlineUsage = registry.getRefactoring(new InlineUsage(registry, usage, initializer));
        inlineUsage.onUpdate.listen(() -> {
            if (isEnabled()) {
                if (!inlineUsage.isEnabled()) {
                    setEnabled(this.usages.stream().anyMatch(Refactoring::isEnabled));
                }
            } else {
                if (inlineUsage.isEnabled()) {
                    setEnabled(true);
                }
            }
        });
        this.usages.add(inlineUsage);
    }

    public boolean areUsagesLeft() {
        boolean usagesLeft = false;
        for (InlineUsage usage : usages) {
            if (!usage.isEnabled()) {
                usagesLeft = true;
                break;
            }
        }
        return usagesLeft;
    }

    @Override
    @NotNull
    public Stream<Refactoring> getRefactorings() {
        return Stream.concat(usages.stream(), Stream.of(remove));
    }

    public IInitializer getInitializer() {
        return initializer;
    }

    public ArrayList<InlineUsage> getUsages() {
        return usages;
    }

    public RemoveElement getRemove() {
        return remove;
    }

    public InlineUsage enableOnly(IVariable toEnable) {
        disableAll();

        InlineUsage enabled = null;
        for (InlineUsage usage : usages) {
            if (enabled == null && usage.getUsage().equals(toEnable)) {
                usage.setEnabled(true);
                enabled = usage;
            } else {
                usage.setEnabled(false);
            }
        }
        remove.setEnabled(!areUsagesLeft());
        return enabled;
    }

    private void disableAll() {
        setEnabled(false);
        for (InlineUsage usage : usages) {
            usage.setEnabled(false);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineAssignment that = (InlineAssignment) o;
        return initializer.equals(that.initializer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initializer);
    }

    public String toString() {
        return "" + initializer.getText();
    }
}
