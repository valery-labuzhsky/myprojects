package streamline.plugin.refactoring.guts;

public abstract class Refactoring {
    protected final RefactoringRegistry registry;
    private boolean enabled = true;

    public Refactoring(RefactoringRegistry registry) {
        this.registry = registry;
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

    protected abstract void doRefactor();

    public RefactoringRegistry getRegistry() {
        return registry;
    }
}
