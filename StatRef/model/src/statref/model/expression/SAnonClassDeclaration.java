package statref.model.expression;

import statref.model.SBaseClassDeclaration;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SAnonClassDeclaration extends SBaseClassDeclaration, SExpression {
    SConstructor getConstructor();
}