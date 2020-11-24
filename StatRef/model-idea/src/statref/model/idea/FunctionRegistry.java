package statref.model.idea;

import java.util.function.Function;

public class FunctionRegistry<K, V> extends ClassRegistry<K, Function<K, V>> {

    public <P extends K> void register(Class<P> key, Function<P, V> value) {
        super.register((Class) key, (Function) value);
    }

    public <T> T convert(Object o) {
        if (o == null) return null;
        Function function = get((Class<K>) o.getClass());
        if (function != null) {
            return (T) function.apply(o);
        }
        throw new RuntimeException("Conversion is not registered for " + o.getClass().getName());
    }
}
