package statref.model.expressions;

import statref.model.fragment.Place;
import statref.model.types.SClass;
import statref.model.types.SType;

import java.util.stream.Stream;

public interface SLiteral extends SExpression {
    Object getValue();

    @Override
    default SType getType() {
        return new SClass(getValue().getClass());
    }

    @Override
    default Object getSignature() {
        return getValue();
    }

    @Override
    default Stream<Place<SExpression>> getExpressions() {
        return Stream.empty();
    }

    @Override
    default String getText() {
        if (getValue() instanceof String) {
            String java = ((String) getValue()).replaceAll("\"", "\\\"");
            return "\"" + java + "\"";
        }
        return "" + getValue();
    }
}
