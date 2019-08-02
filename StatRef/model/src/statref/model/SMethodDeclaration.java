package statref.model;

import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SMethodDeclaration extends SModifiers, SClassMemeber, SElement {

    List<? extends SParameter> getParameters();

    String getName();

    SType getReturnType();

    List<SStatement> getInstructions();

    default boolean isVoid() {
        SType type = getReturnType();
        if (type instanceof SPrimitive) {
            return ((SPrimitive) type).getJavaClass()==void.class;
        } else {
            return false;
        }
    }
}
