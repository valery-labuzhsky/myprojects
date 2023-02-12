package statref.api;

import java.lang.reflect.Type;

/**
 * Created on 17/12/17.
 *
 * @author ptasha
 */
public interface Field<O, V> extends Getter<O, V>, Setter<O, V> {
    Class<O> getObjectType();

    Type getValueType();
}
