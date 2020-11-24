package statref.model.expressions;

import statref.model.fragment.Place;

import java.util.stream.Stream;

public interface SLiteral extends SExpression {
    Object getValue();

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
            return "\""+ java +"\"";
        }
        return ""+ getValue();
    }
}
