package statref.model;

public interface SElement {

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
