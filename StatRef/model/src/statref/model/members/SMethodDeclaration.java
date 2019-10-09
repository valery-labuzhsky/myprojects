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
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parameters);
        }
    }
}
