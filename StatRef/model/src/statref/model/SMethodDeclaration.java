package statref.model;

import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SMethodDeclaration extends SModifiers, SClassMemeber, STraceContext {

    List<SBaseVariableDeclaration> getParameters();

    String getName();

    SType getReturnType();

    List<SInstruction> getInstructions();

    @Override
    default SContext getContext() {
        return SClassMemeber.super.getContext();
    }
}
