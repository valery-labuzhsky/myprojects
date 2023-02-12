package statref.model.expressions;

import statref.model.classes.SBaseClassDeclaration;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SAnonClassDeclaration extends SBaseClassDeclaration, SExpression {
    SConstructor getConstructor();
}
