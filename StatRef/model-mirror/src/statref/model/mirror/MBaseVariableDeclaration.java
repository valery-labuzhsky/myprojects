package statref.model.mirror;

import statref.model.members.SBaseVariableDeclaration;
import statref.model.types.SType;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collection;

/**
 * Created on 03/02/18.
 *
 * @author ptasha
 */
public abstract class MBaseVariableDeclaration extends MElement<VariableElement> implements SBaseVariableDeclaration {

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
    public Collection<Modifier> getModifiers() {
        return getElement().getModifiers();
    }

}
