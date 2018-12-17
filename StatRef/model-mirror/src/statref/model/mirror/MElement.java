package statref.model.mirror;

import statref.model.SElement;

import javax.lang.model.element.Element;

public abstract class MElement<E extends Element> implements SElement {
    private final E element;

    public MElement(E element) {
        this.element = element;
    }

    public E getElement() {
        return element;
    }

    @Override
    public SElement getParent() {
        throw new UnsupportedOperationException();
    }
}
