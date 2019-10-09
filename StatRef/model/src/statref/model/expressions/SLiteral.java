package statref.model.expressions;

import statref.model.fragment.Place;

import java.util.Collections;
import java.util.List;

public interface SLiteral extends SExpression {
    Object getValue();

    @Override
    default Object getSignature() {
        return getValue();
    }

    @Override
    default List<Place<SExpression>> getExpressions() {
        return Collections.emptyList();
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
