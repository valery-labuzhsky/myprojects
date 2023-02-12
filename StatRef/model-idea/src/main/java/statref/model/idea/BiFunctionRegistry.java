package statref.model.idea;

import java.util.function.BiFunction;

public class BiFunctionRegistry<K, U, R> extends ClassRegistry<K, BiFunction<K, U, R>> {

    public <P extends K> void register(Class<P> key, BiFunction<P, U, R> value) {
        super.register((Class) key, (BiFunction) value);
    }

    public <T extends R> T convert(K o, U u) {
        BiFunction<K, U, R> function = get((Class<K>) o.getClass());
        if (function != null) {
            return (T) function.apply(o, u);
        }
        return null;
    }
}
