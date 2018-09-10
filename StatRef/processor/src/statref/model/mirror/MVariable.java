package statref.model.mirror;

import statref.model.SExpression;
import statref.model.SVariable;
import statref.model.SType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collection;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public class MVariable implements SVariable {
    private final VariableElement element;

    public MVariable(VariableElement element) {
        this.element = element;
    }

    @Override
    public SType getType() {
        return MType.get(element.asType());
    }

    @Override
    public SExpression getExpression() {
        return null;
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return element.getModifiers();
    }
}
