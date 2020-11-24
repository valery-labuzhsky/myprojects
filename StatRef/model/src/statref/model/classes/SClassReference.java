package statref.model.classes;

import statref.model.SElement;
import statref.model.types.SType;

import java.util.List;

/**
 * Created on 24.11.2020.
 *
 * @author unicorn
 */
public interface SClassReference extends SElement {
    boolean isDiamond();

    List<SType> resolveParameters();

    List<SType> getParameters();

    void setParameters(List<SType> parameters);

    String getSimpleName();
}
