package statref.model.idea;

import statref.model.SType;

interface PlaceAdapter<F extends Fragment, T> extends Place<T>, InternalPlace<F, T> {
    @Override
    default String getName(Fragment fragment) {
        return _getName((F) fragment);
    }

    @Override
    default SType getType(Fragment fragment) {
        return _getType((F) fragment);
    }

    @Override
    default T get(Fragment fragment) {
        return _get((F) fragment);
    }

    @Override
    default void set(Fragment fragment, T value) {
        _set((F) fragment, value);
    }
}
