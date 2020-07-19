package statref.model.mirror;

import statref.model.members.SVariableDeclaration;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collection;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public abstract class MVariableDeclaration extends MElement<VariableElement> implements SVariableDeclaration {

    public MVariableDeclaration(VariableElement element) {
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
    public Collection<Modifier> getModifiers() {
        return getElement().getModifiers();
    }

}
