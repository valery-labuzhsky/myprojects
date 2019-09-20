package statref.model.idea;

import statref.model.SType;
import statref.model.expression.SExpression;

public class FragmentPlace<T> {
    private final InternalFragmentPlace<CodeFragment, T> internal;

    public <F extends CodeFragment> FragmentPlace(InternalFragmentPlace<F, T> internal) {
        this.internal = (InternalFragmentPlace<CodeFragment, T>) internal;
    }

    public String getName(CodeFragment fragment) {
        return internal._getName(fragment);
    }

    public SType getType(CodeFragment fragment) {
        return internal._getType(fragment);
    }

    public T get(CodeFragment fragment) {
        return internal._get(fragment);
    }

    public void set(CodeFragment fragment, T value) {
        internal._set(fragment, value);
    }

    interface Place<T> {
        String getName(CodeFragment fragment);

        SType getType(CodeFragment fragment);

        T get(CodeFragment fragment);

        void set(CodeFragment fragment, T value);
    }

    interface PlaceAdapter<F extends CodeFragment, T> extends Place<T>, InternalFragmentPlace<F, T> {
        @Override
        default String getName(CodeFragment fragment) {
            return _getName((F) fragment);
        }

        @Override
        default SType getType(CodeFragment fragment) {
            return _getType((F) fragment);
        }

        @Override
        default T get(CodeFragment fragment) {
            return _get((F) fragment);
        }

        @Override
        default void set(CodeFragment fragment, T value) {
            _set((F) fragment, value);
        }
    }
}
