package statref.model.expressions;

import statref.model.types.SType;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public interface SField extends SReference {
    SType getQualifier();
}
