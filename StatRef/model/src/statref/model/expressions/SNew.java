package statref.model.expressions;

import statref.model.classes.SClassReference;

/**
 * Created on 23.11.2020.
 *
 * @author unicorn
 */
public interface SNew extends SCall {
    SClassReference getClassReference();
}
