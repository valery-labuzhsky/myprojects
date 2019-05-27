package streamline.plugin.refactoring;

public abstract class Refactoring {
    private boolean enabled = true;

    public void refactor() {
        if (isEnabled()) {
            doRefactor();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected abstract void doRefactor();
}
