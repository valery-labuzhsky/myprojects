package statref.model.fragment;

import statref.model.types.SType;

public interface InternalPlace<F extends Fragment, T> {
    String _getName(F fragment);

    SType _getType(F fragment);

    T _get(F fragment);

    void _set(F fragment, T value);

}
