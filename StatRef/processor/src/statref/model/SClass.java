package statref.model;

import java.util.List;

public interface SClass extends SType {
    SPackage getPackage();

    String getSimpleName();

    List<SType> getGenerics();
}
