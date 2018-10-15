package statref.model;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public interface SFieldUsage extends SExpression {
    SType getType();

    String getName();
}
