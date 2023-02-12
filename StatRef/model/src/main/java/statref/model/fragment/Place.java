package statref.model.fragment;

import statref.model.types.SType;

public interface Place<T> {
    String getName(Fragment fragment);

    SType getType(Fragment fragment);

    T get(Fragment fragment);

    void set(Fragment fragment, T value);
}
