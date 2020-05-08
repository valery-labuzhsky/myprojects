package streamline.plugin.refactoring;

import statref.model.idea.IInitializer;
import statref.model.idea.IVariable;
import streamline.plugin.refactoring.guts.Refactoring;
import streamline.plugin.refactoring.guts.RefactoringRegistry;
import streamline.plugin.refactoring.guts.flow.VariableFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InlineUsage extends Refactoring {
    // TODO basically, I have the same refactoring in both branches, but only one can be applied
    //  Not very healthy situation
    //  So I have other variants which are depend on other branches
    //  So I have dependency between different branches, anyway
    //  I can either do it like I chose to or I can simplify inline usage, but connect them and
    //  create some complexity to deal with situation
    //  I must have this complexity on refactoring level as well to be able to analyse it later
    //  what is the best way?

    // TODO because it's the same refactoring I'm forced to move enabled/disabled flag on the parent
    //  where it does not belong
    //  how to make it better?
    //  I can have inline usage with only one variant and links to other variants
    //  what if I don't have any decision yet?
    //  is there any good way to display it at all?
    //  why! it will be disabled, but clicking some value will enable it somewhere else
    //  this way I maybe will have less craziness

    // TODO the task: have only one variant and a bunch of usages connected to it
    //  how would I achieve it?
    //  1. I need have separate usages which will listen and mirror each other
    //  For that I need to include variable in comparison
    //  but I need to find the neighbours
    //  registry actually fits there perfectly
    //  let's register all refactorings per element
    //  or I'll create special logic just for usage?
    //  doesn't make any sense
    private final IVariable usage;
    private final IInitializer value;
    private IInitializer selected;
    private final ArrayList<IInitializer> variants = new ArrayList<>();

    public InlineUsage(RefactoringRegistry registry, IVariable usage, IInitializer value) {
        super(registry, usage);
        this.usage = usage;
        this.value = value;
        this.variants.addAll(new VariableFlow(usage).getAssignments(usage));
        setSelected(value);
        if (variants.size() > 1) {
            setEnabled(false);
        }
    }

    @Override
    protected void doRefactor() {
        if (selected != null) {
            usage.replace(selected.getInitializer());
        }
        setEnabled(false); // TODO kludge to get rid of recursion
        for (Refactoring refactoring : whatElse()) {
            refactoring.refactor();
        }
    }

    public IVariable getUsage() {
        return usage;
    }

    public ArrayList<IInitializer> getVariants() {
        return variants;
    }

    public IInitializer getSelected() {
        return selected;
    }

    public void setSelected(IInitializer variant) {
        selected = variant;
    }

    public List<Refactoring> whatElse() {
        // TODO all this whatElse staff causing too much of recursion
        // TODO what can I do with it?

        // TODO the question again: groing tree vs strict structure
        // TODO structure is better - it offers single view - no need to having weird trees like inlining one value inside of another
        // TODO in the brain too
        // TODO it will simplify things a lot!

        // TODO so I need to fold it out not only down but up as well
        // TODO for it I need inline variablle refactoring
        // TODO should I jump users, probably not

        // TODO it will simplify this whatElse staff a lot!
        // TODO I will have clear dependencies

        // TODO first things first - I need inline variable
        // TODO it will contain assignments, they will contain usages
        ArrayList<Refactoring> refactorings = new ArrayList<>();
        whatElse(refactorings);
        return refactorings;
    }

    private void whatElse(ArrayList<Refactoring> refactorings) {
        if (selected != null) {
            for (IInitializer variant : variants) {
                refactorings.add(registry.getRefactoring(new InlineAssignment(registry, variant)));
            }
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
        InlineUsage usage = (InlineUsage) o;
        return this.usage.equals(usage.usage) &&
                value.equals(usage.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usage, value);
    }

    @Override
    public String toString() {
        return "Replace " + usage.getText() + " with " + (selected == null ? null : selected.getText());
    }
}
