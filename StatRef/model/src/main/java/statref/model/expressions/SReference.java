package statref.model.expressions;

import statref.model.fragment.Place;

import java.util.stream.Stream;

/**
 * Created on 19.07.2020.
 *
 * @author unicorn
 */
public interface SReference extends SExpression {
    String getName();

    @Override
    default Stream<Place<SExpression>> getExpressions() {
        return Stream.empty();
    }

    @Override
    default Object getSignature() {
        return this;
    }
}
