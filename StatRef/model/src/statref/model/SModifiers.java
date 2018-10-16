package statref.model;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Set;

/**
 * Created on 28/01/18.
 *
 * @author ptasha
 */
public interface SModifiers {
    default boolean isPublic() {
        return getModifiers().contains(Modifier.PUBLIC);
    }

    default boolean isStatic() {
        return getModifiers().contains(Modifier.STATIC);
    }

    Collection<Modifier> getModifiers(); // TODO I probably need my own Modifier
}
