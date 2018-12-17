package statref.model.mirror;

import statref.model.SBaseVariableDeclaration;
import statref.model.SType;
import statref.model.expression.SExpression;
import statref.model.expression.SVariable;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collection;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public class MBaseVariableDeclaration extends MElement<VariableElement> implements SBaseVariableDeclaration {

    public MBaseVariableDeclaration(VariableElement element) {
        super(element);
    }

    @Override
    public SType getType() {
        return MBase.get(getElement().asType());
    }

    @Override
    public String getName() {
        return getElement().getSimpleName().toString();
    }

    @Override
    public SExpression getInitializer() {
        return null;
    }

    @Override
    public SVariable usage() {
        return new MVariable(getElement());
    }

    @Override
    public Collection<Modifier> getModifiers() {
        return getElement().getModifiers();
    }

}
