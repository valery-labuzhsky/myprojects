package statref.model.mirror;

import statref.model.SExpression;
import statref.model.SVariable;
import statref.model.SBaseVariableDeclaration;
import statref.model.SType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collection;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public class MBaseVariableDeclaration implements SBaseVariableDeclaration {
    private final VariableElement element;

    public MBaseVariableDeclaration(VariableElement element) {
        this.element = element;
    }

    @Override
    public SType getType() {
        return MBase.get(element.asType());
    }

    @Override
    public String getName() {
        return element.getSimpleName().toString();
    }

    @Override
    public SExpression getExpression() {
        return null;
    }

    @Override
    public SVariable usage() {
        return new MVariable(element);
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return element.getModifiers();
    }
}
