package statref.model.classes;

import statref.model.types.SClass;
import statref.model.members.SClassMemeber;
import statref.model.members.SMethodDeclaration;

import java.util.List;
import java.util.stream.Collectors;

public interface SBaseClassDeclaration {
    List<SClassMemeber> getMembers();

    default List<SMethodDeclaration> getMethods() {
        return getMembers().stream().
                filter(memeber -> memeber instanceof SMethodDeclaration).
                map(memeber -> (SMethodDeclaration) memeber).
                collect(Collectors.toList());
    }

    SClass usage();
}
