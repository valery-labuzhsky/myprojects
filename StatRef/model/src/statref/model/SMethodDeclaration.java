package statref.model;

import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SMethodDeclaration extends SModifiers, SClassMemeber {

    List<SBaseVariableDeclaration> getParameters();

    String getName();

    SType getReturnType();

    List<SInstruction> getInstructions();
}
