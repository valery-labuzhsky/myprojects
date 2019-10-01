package statref.model.idea;

import statref.model.SType;

public interface InternalPlace<F extends Fragment, T> {
    String _getName(F fragment);

    SType _getType(F fragment);

    T _get(F fragment);

    void _set(F fragment, T value);

}
