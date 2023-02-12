package statref.model.builder.expressions;

import statref.model.classes.SClassReference;
import statref.model.expressions.SNew;

/**
 * Created on 24.11.2020.
 *
 * @author unicorn
 */
@SuppressWarnings("unused")
public class BNew extends BCall implements SNew {

    @SuppressWarnings("unused")
    public BNew(SNew newCall) {
        super(newCall);
    }

    @Override
    public SClassReference getClassReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public String toString() {
        return "new " + super.toString();
    }
}
