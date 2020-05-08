package streamline.plugin.refactoring.guts;

import statref.model.SElement;

public abstract class Refactoring {
    protected final RefactoringRegistry registry;
    private final SElement element;
    private boolean enabled = true;

    public Refactoring(RefactoringRegistry registry, SElement element) {
        this.registry = registry;
        this.element = element;
    }

    public void refactor() {
        if (isEnabled()) {
            doRefactor();
            setEnabled(false);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return false;
        } else {
            this.enabled = enabled;
            return true;
        }
    }

    public SElement getElement() {
        return element;
    }

    protected abstract void doRefactor();

    public RefactoringRegistry getRegistry() {
        return registry;
    }
}
