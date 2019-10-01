package statref.model;

import java.util.Collections;
import java.util.List;

public interface SClass extends SType {
    // TODO it should actually always know its package or inclosing class from the context

    String getName();

    default List<SType> getGenerics() {
        return Collections.emptyList();
    }

    default Class<?> getJavaClass() {
        return null;
    }
}
