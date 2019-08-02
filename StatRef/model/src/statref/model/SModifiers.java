package statref.model;

import javax.lang.model.element.Modifier;
import java.util.Collection;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SModifiers { // TODO common modifiers for all is dumb,
    // TODO we need separate modifiers for visibility, static/not, final/not
    default boolean isPublic() {
        return getModifiers().contains(Modifier.PUBLIC);
    }

    default boolean isStatic() {
        return getModifiers().contains(Modifier.STATIC);
    }

    Collection<Modifier> getModifiers();
}
