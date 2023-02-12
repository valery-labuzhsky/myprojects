package statref.model.classes;

import statref.model.members.SClassMemeber;
import statref.model.members.SMethodDeclaration;
import statref.model.types.SClass;

import java.util.List;
import java.util.stream.Collectors;

public interface SBaseClassDeclaration {
    default List<SClassMemeber> getMembers() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    default List<SMethodDeclaration> getMethods() {
        return getMembers().stream().
                filter(memeber -> memeber instanceof SMethodDeclaration).
                map(memeber -> (SMethodDeclaration) memeber).
                collect(Collectors.toList());
    }

    default SClass usage() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
