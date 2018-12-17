package statref.model;

public interface SElement {
    default SContext getContext() {
        SElement parent = getParent();
        while (parent !=null && !(parent instanceof SContext)) {
            parent = getParent();
        }
        return (SContext) parent;
    }

    SElement getParent();
}
