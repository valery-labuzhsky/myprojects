package statref.model;

import statref.model.mirror.MVariable;

import java.util.List;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SMethod extends SModifiers {

    List<MVariable> getParameters();

    String getName();

    SType getReturnType();

    List<SInstruction> getInstructions();
}
