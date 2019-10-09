package statref.model.builder.expressions;

import statref.model.expressions.SLiteral;

public class BLiteral implements SLiteral {
    private Object value;

    public BLiteral(SLiteral literal) {
        this.value = literal.getValue();
    }

    @Override
    public Object getValue() {
        return value;
    }

}
