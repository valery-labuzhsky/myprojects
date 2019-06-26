package streamline.plugin.refactoring;

public abstract class Refactoring {
    protected final RefactoringRegistry registry;
    private boolean enabled = true;

    public Refactoring(RefactoringRegistry registry) {
        this.registry = registry;
    }

    public void refactor() {
        if (isEnabled()) {
            doRefactor();
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

    protected abstract void doRefactor();
}
