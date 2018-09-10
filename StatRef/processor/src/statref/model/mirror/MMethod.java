package statref.model.mirror;

import statref.model.SInstruction;
import statref.model.SMethod;
import statref.model.SType;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public class MMethod implements SMethod {
    private final ExecutableElement element;

    public MMethod(ExecutableElement element) {
        this.element = element;
    }

    @Override
    public List<MVariable> getParameters() {
        ArrayList<MVariable> parameters = new ArrayList<>();
        for (VariableElement element : this.element.getParameters()) {
            parameters.add(new MVariable(element));
        }
        return parameters;
    }

    @Override
    public String getName() {
        return this.element.getSimpleName().toString();
    }

    @Override
    public Set<Modifier> getModifiers() {
        return this.element.getModifiers();
    }

    @Override
    public SType getReturnType() {
        return MType.get(element.getReturnType());
    }

    @Override
    public List<SInstruction> getInstructions() {
        return Collections.emptyList();
    }
}
