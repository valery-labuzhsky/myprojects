package statref.model.members;

import statref.model.*;
import statref.model.statements.SStatement;
import statref.model.types.SType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created on 27/01/18.
 *
 * @author ptasha
 */
public interface SMethodDeclaration extends SModifiers, SClassMemeber, SElement {

    List<? extends SParameter> getParameters();

    String getName();

    SType getReturnType();

    List<SStatement> getInstructions();

    @Override
    default Signature getSignature() {
        return new Signature(this);
    }

    class Signature {
        private final String name;
        private final List<SType> parameters;

        public Signature(SMethodDeclaration method) {
            name = method.getName();
            parameters = method.getParameters().stream().
                    map(SBaseVariableDeclaration::getType).
                    collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Signature signature = (Signature) o;
            return name.equals(signature.name) &&
                    parameters.equals(signature.parameters);
            // TODO how to compare types?
            // TODO I need a signature for them since I have different type implementations
            // TODO do I need different types?
            // TODO it's very useful to implement them this way, but not at all to work with
            // TODO transform them to common ground?
            // TODO signatures are becoming overcomplicated this way
            // TODO at least types I need to compare
            // TODO I need common types and different transformers for them
            // TODO how can I solve it?
            // TODO so the type must be the only one, but there should be different factories
            // TODO type will have many constructors
            // TODO make type buildable? what for?
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parameters);
        }
    }
}
