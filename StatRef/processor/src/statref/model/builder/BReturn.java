package statref.model.builder;

import statref.model.SClass;
import statref.model.SExpression;
import statref.model.SReturn;

/**
 * Created on 04/02/18.
 *
 * @author ptasha
 */
public class BReturn implements SReturn {
    private final SExpression expression;

    public BReturn(SExpression expression) {
        this.expression = expression;
    }

    @Override
    public SExpression getExpression() {
        return expression;
    }

    public static class Builder {
        public BReturn field(SClass clazz, String fieldName) {
            return new BReturn(BBase.field(clazz, fieldName));
        }
    }
}
