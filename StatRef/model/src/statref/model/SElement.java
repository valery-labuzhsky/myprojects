package statref.model;

import statref.model.fragment.Fragment;

public interface SElement extends Fragment {

    default boolean before(SElement element) {
        throw new UnsupportedOperationException();
    }

    default boolean after(SElement element) {
        throw new UnsupportedOperationException();
    }

    default SElement getParent() {
        throw new UnsupportedOperationException();
    }

    default String getText() {
        return toString();
    }

}
