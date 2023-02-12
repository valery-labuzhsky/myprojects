package statref.model.mirror;

import statref.model.classes.SPackage;

import javax.lang.model.element.Element;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public class MPackage implements SPackage {
    private final Element element;

    public MPackage(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
