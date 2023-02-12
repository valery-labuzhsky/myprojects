package statref.model.builder;

import statref.model.SElement;

public abstract class BElement implements SElement {
    @Override
    public SElement getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText() {
        return BBase.write(this);
    }

}
