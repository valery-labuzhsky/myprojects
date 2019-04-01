package statref.model;

public interface SElement {
    default SContext getContext() {
        SElement parent = getParent();
        if (parent != null) {
            return parent.getContext(this);
        }
        return null;
    }

    default SContext getContext(SElement element) {
        if (this instanceof SContext) {
            return (SContext) this;
        }
        return getContext();
    }

    default STraceContext getTraceContext() {
        SContext context = getContext();
        while (context != null) {
            if (context instanceof STraceContext) {
                return (STraceContext) context;
            }
            context = context.getContext();
        }
        return null;
    }

    default boolean before(SElement element) {
        throw new UnsupportedOperationException();
    }

    default boolean after(SElement element) {
        return !before(element);
    }

    default SElement getParent() {
        throw new UnsupportedOperationException();
    }

    default String getText() {
        return toString();
    }
}
