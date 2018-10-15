package statref.model;

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
